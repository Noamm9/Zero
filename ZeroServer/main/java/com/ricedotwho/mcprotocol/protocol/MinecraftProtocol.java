package com.ricedotwho.mcprotocol.protocol;

import com.ricedotwho.mcprotocol.protocol.net.*;
import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
import com.ricedotwho.mcprotocol.protocol.net.pipeline.PacketDecoder;
import com.ricedotwho.mcprotocol.protocol.net.pipeline.PacketEncoder;
import com.ricedotwho.mcprotocol.protocol.net.pipeline.VarIntFrameDecoder;
import com.ricedotwho.mcprotocol.protocol.net.pipeline.VarIntFrameEncoder;
import com.ricedotwho.mcprotocol.protocol.net.registry.PacketDirection;
import com.ricedotwho.zero.Zero;
import com.ricedotwho.zero.module.Module;
import com.ricedotwho.zero.ws.SocketClient;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import io.netty.channel.*;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.geysermc.mcprotocollib.auth.GameProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MinecraftProtocol {
    @Getter
    private static final Map<GameProfile, MinecraftClient> onlinePlayers = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(MinecraftProtocol.class);

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel channel;

    public void start(int listenPort) throws InterruptedException {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel clientChannel) {
                        clientChannel.config().setTcpNoDelay(true);
                        clientChannel.pipeline()
                                .addLast("splitter", new VarIntFrameDecoder())
                                .addLast("prepender", new VarIntFrameEncoder())
                                .addLast("decoder", new PacketDecoder(PacketDirection.SERVERBOUND))
                                .addLast("encoder", new PacketEncoder(PacketDirection.CLIENTBOUND))
                                .addLast("handler", new SessionHandler(clientChannel));
                    }
                });


        ChannelFuture bindFuture = serverBootstrap.bind(listenPort).sync();
        log.info("Proxy started on: {}", listenPort);
        this.channel = bindFuture.channel();
        this.channel.closeFuture().sync();
    }

    public void stop() {
        for (MinecraftClient client : new ArrayList<>(onlinePlayers.values())) {
            client.getMODULES().values().forEach(Module::onDisable);
            client.disconnect(Component.text("Proxy Stopped").color(NamedTextColor.RED));
        }

        if (channel != null) {
            channel.close().syncUninterruptibly();
            channel = null;
            log.info("serverChannel closed");
        }

        if (bossGroup != null) {
            bossGroup.shutdownGracefully().syncUninterruptibly();
            bossGroup = null;
            log.info("bossGroup shutdown");
        }

        if (workerGroup != null) {
            workerGroup.shutdownGracefully().syncUninterruptibly();
            workerGroup = null;
            log.info("workerGroup shutdown");
        }
    }

    public static void playerJoined(GameProfile profile, MinecraftClient client) {
        onlinePlayers.put(profile, client);
        SocketClient sc = Zero.getSocketClient();
        sc.updateCount(onlinePlayers.size());
        sc.addServer(client.getInfo().getHost());
    }

    public static void playerLeft(GameProfile profile, MinecraftClient client) {
        onlinePlayers.remove(profile);
        SocketClient sc = Zero.getSocketClient();
        sc.updateCount(onlinePlayers.size());
        if (client != null) sc.removeServer(client.getInfo().getHost());

        if (Zero.isNeedsUpdate() && getPlayerCount() == 0) {
            Zero.stopProxy();
        }
    }

    public static int getPlayerCount() {
        return onlinePlayers.size();
    }
}

