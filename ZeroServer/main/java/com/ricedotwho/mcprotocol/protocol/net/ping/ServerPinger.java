package com.ricedotwho.mcprotocol.protocol.net.ping;


import com.ricedotwho.mcprotocol.Constants;
import com.ricedotwho.mcprotocol.protocol.net.pipeline.*;
import com.ricedotwho.mcprotocol.protocol.net.registry.PacketDirection;
import com.ricedotwho.mcprotocol.protocol.packet.handshake.serverbound.ServerboundClientIntentionPacket;
import com.ricedotwho.mcprotocol.protocol.packet.status.severbound.ServerboundStatusRequestPacket;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.Getter;
import org.geysermc.mcprotocollib.protocol.data.ProtocolState;
import org.geysermc.mcprotocollib.protocol.data.handshake.HandshakeIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ricedotwho.mcprotocol.protocol.net.SessionHandler.PROTOCOL_STATE;

public class ServerPinger {
    private static final Logger log = LoggerFactory.getLogger(ServerPinger.class);
    private static final EventLoopGroup PING_GROUP = new NioEventLoopGroup(1);

    @Getter
    private final String host;
    @Getter
    private final int port;
    private Channel channel;

    public ServerPinger(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void ping() {
        Bootstrap bootstrap = new Bootstrap()
                .group(PING_GROUP)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast("splitter", new VarIntFrameDecoder());
                        p.addLast("prepender", new VarIntFrameEncoder());
                        p.addLast("decoder", new PacketDecoder(PacketDirection.CLIENTBOUND));
                        p.addLast("encoder", new PacketEncoder(PacketDirection.SERVERBOUND));
                        p.addLast("readTimeout", new ReadTimeoutHandler(5));
                        p.addLast("handler", new PingHandler(ServerPinger.this));
                    }
                });

        bootstrap.connect(host, port).addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                log.info("{} failed to connect! ({})", future.channel().localAddress(), future.cause().getMessage());
                return;
            }

            this.channel = future.channel();

            this.channel.attr(PROTOCOL_STATE).set(ProtocolState.HANDSHAKE);
            this.channel.writeAndFlush(new ServerboundClientIntentionPacket(Constants.PROTOCOL_VERSION, this.host, this.port, HandshakeIntent.STATUS));
            this.channel.attr(PROTOCOL_STATE).set(ProtocolState.STATUS);
            this.channel.writeAndFlush(new ServerboundStatusRequestPacket());

            channel.closeFuture();
        });
    }

    public void disconnect() {
        if (channel != null && channel.isOpen()) {
            channel.flush().disconnect();
        }
    }
}