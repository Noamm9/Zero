package com.ricedotwho.mcprotocol.protocol.net.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ricedotwho.mcprotocol.Constants;
import com.ricedotwho.mcprotocol.protocol.net.SessionHandler;
import com.ricedotwho.mcprotocol.protocol.net.pipeline.*;
import com.ricedotwho.mcprotocol.protocol.net.registry.PacketDirection;
import com.ricedotwho.mcprotocol.protocol.net.session.ClientSession;
import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import com.ricedotwho.mcprotocol.protocol.packet.common.clientbound.ClientboundCustomPayloadPacket;
import com.ricedotwho.mcprotocol.protocol.packet.handshake.serverbound.ServerboundClientIntentionPacket;
import com.ricedotwho.mcprotocol.protocol.packet.login.severbound.ServerboundHelloPacket;
import com.ricedotwho.mcprotocol.protocol.packet.login.severbound.ServerboundKeyPacket;
import com.ricedotwho.mcprotocol.protocol.util.Util;
import com.ricedotwho.mcprotocol.utils.ByteBufUtils;
import com.ricedotwho.zero.Zero;
import com.ricedotwho.zero.event.EventBus;
import com.ricedotwho.zero.module.Module;
import com.ricedotwho.zero.util.ConnectionInfo;
import com.ricedotwho.zero.util.DataLoader;
import com.ricedotwho.zero.util.Island;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.geysermc.mcprotocollib.auth.GameProfile;
import org.geysermc.mcprotocollib.protocol.data.ProtocolState;
import org.geysermc.mcprotocollib.protocol.data.handshake.HandshakeIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.*;

public class MinecraftClient {
    private static final Logger log = LoggerFactory.getLogger(MinecraftClient.class);

    @Getter
    private final EventBus EVENT_BUS = new EventBus();
    @Getter
    private final Map<Class<?>, Module> MODULES = new HashMap<>();

    @Getter
    private final ConnectionInfo info;
    @Getter
    @Setter
    private GameProfile profile;
    @Getter
    @Setter
    private SecretKey key;
    @Getter
    @Setter
    private ClientSession session;
    @Getter
    private final ClientSession remoteSession;
    @Getter
    private PacketEncryption encryption;
    @Getter
    @Setter
    private Island area = Island.Unknown;
    @Getter
    private final SessionHandler otherHandler;
    @Getter
    private final List<Packet> queue = new ArrayList<>();
    @Setter
    public KeyData keyData;
    @Getter
    private boolean ready = false;

    private EventLoopGroup group;

    public MinecraftClient(ConnectionInfo info, SessionHandler otherHandler) {
        this.info = info;
        String username = Util.getUsername(info.getUuid());
        this.profile = new GameProfile(info.getUuid(), username);

        this.remoteSession = new ClientSession(this.info.getHost(), this.info.getPort());
        this.otherHandler = otherHandler;
    }

    public void connect() {
        group = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
        final Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.config().setTcpNoDelay(true);
                        ch.pipeline()
                                .addLast("encryption", new TcpPacketEncryptor(MinecraftClient.this))
                                .addLast("splitter", new VarIntFrameDecoder())
                                .addLast("prepender", new VarIntFrameEncoder())
                                .addLast("decoder", new PacketDecoder(PacketDirection.CLIENTBOUND))
                                .addLast("encoder", new PacketEncoder(PacketDirection.SERVERBOUND))
                                .addLast("handler", new ClientHandler(MinecraftClient.this));
                    }
                });

        bootstrap.connect(info.getHost(), info.getPort()).addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                //future.cause().printStackTrace();
                group.shutdownGracefully();
                log.info("{} failed to connect! ({})", future.channel().localAddress(), future.cause().getMessage());
                this.disconnect(Component.text("Proxy failed to connect to server. \nCause: " + future.cause().getMessage()).color(TextColor.color(255, 0, 0)), true);
                return;
            }

            Channel channel = future.channel();
            this.getRemoteSession().setChannel(channel);
            this.getRemoteSession().setState(ProtocolState.HANDSHAKE);

            sendIntentionAndHello();

            channel.closeFuture().addListener(cf -> group.shutdownGracefully());
        });
    }

    public void disconnect(Component reason) {
        this.disconnect(reason, false);
    }

    public void disconnect(Component reason, boolean login) {
        this.getSession().disconnect(reason, login);
        this.getRemoteSession().disconnect(reason, login);
    }

    public void sendPacket(Packet packet) {
        this.getRemoteSession().send(packet);
    }

    public void sendIntentionAndHello() {
        this.sendPacket(new ServerboundClientIntentionPacket(Constants.PROTOCOL_VERSION, info.getHost(), info.getPort(), HandshakeIntent.LOGIN));
        this.getRemoteSession().setState(ProtocolState.LOGIN);
        this.sendPacket(new ServerboundHelloPacket(this.profile.getName(), this.profile.getId()));
    }

    public void setCompressionThreshold(int threshold) {
        if (this.remoteSession.getChannel() == null) return;

        if (threshold >= 0) {
            if (this.remoteSession.getChannel().pipeline().get("compression") != null) {
                this.remoteSession.getChannel().pipeline().remove("compression");
            }

            this.remoteSession.getChannel().pipeline().addBefore("decoder", "compression",
                    new TcpPacketCompression(threshold, DataLoader.getData().isValidateDecompression()));

        } else {
            if (this.remoteSession.getChannel().pipeline().get("compression") != null) {
                this.remoteSession.getChannel().pipeline().remove("compression");
            }
        }
    }

    public void enableEncryption() {
        try {
            this.encryption = new AESEncryption(this.key);
        } catch(GeneralSecurityException e) {
            throw new Error("Failed to enable protocol encryption.", e);
        }
    }

    public void onLoginDone() {
        Zero.getModules(this);
        JsonArray config = getConfig();
        byte[] bytes = ByteBufUtils.writeString(config.toString());
        //this.getSession().send(new ClientboundSystemChatPacket(Zero.getPrefix().append(Component.text("Transferred!")), false));
        log.info("Login Done!");

        // pmo
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                this.getSession().send(new ClientboundCustomPayloadPacket(Key.key("zero", "config/request_update"), bytes));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public JsonArray getConfig() {
        JsonArray array = new JsonArray();
        for (Module module : this.getMODULES().values()) {
            if (Zero.getIgnoredConfig().contains(module.getName())) continue;
            JsonObject obj = module.getAsJson();;
            if (obj == null) continue;
            array.add(obj);
        }
        return array;
    }

    public <T extends Module> T getModule(Class<T> module) {
        Module m = MODULES.get(module);
        return module.cast(m);
    }

    public void transfer(String host, int port) {
        this.info.setHost(host);
        this.info.setPort(port);
        this.remoteSession.transfer(host, port);
        this.connect();
    }

    public void onSessionReady() {
        for (Packet packet : queue) {
            this.session.send(packet);
        }
        queue.clear();
        ready = true;
    }

    public void onAuthCompleted() {
        // what the fuck is going on
        this.remoteSession.send(new ServerboundKeyPacket(this.keyData.pub(), this.getKey(), this.keyData.challenge()), this::enableEncryption);
    }

    public record KeyData(PublicKey pub, byte[] challenge) {
    }
}