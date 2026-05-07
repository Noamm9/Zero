package com.ricedotwho.mcprotocol.protocol.net.session;

import com.ricedotwho.mcprotocol.protocol.MinecraftProtocol;
import com.ricedotwho.mcprotocol.protocol.net.pipeline.TcpPacketCompression;
import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import com.ricedotwho.mcprotocol.protocol.packet.common.clientbound.ClientboundDisconnectPacket;
import com.ricedotwho.mcprotocol.protocol.packet.login.clientbound.ClientboundLoginDisconnectPacket;
import com.ricedotwho.zero.util.ChatUtil;
import com.ricedotwho.zero.util.DataLoader;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.geysermc.mcprotocollib.auth.GameProfile;
import org.geysermc.mcprotocollib.protocol.data.ProtocolState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import static com.ricedotwho.mcprotocol.protocol.net.SessionHandler.PROTOCOL_STATE;

@Getter
@Setter
public class ClientSession implements Session {
    private static final Logger log = LoggerFactory.getLogger(ClientSession.class);
    private String host;
    private int port;
    @Setter
    private Channel channel;
    private SocketAddress localAddress;
    private final SocketAddress remoteAddress;
    private int threshold = -1;

    @Getter
    @Setter
    private GameProfile profile;

    public ClientSession(String host, int port) {
        this.host = host;
        this.port = port;
        this.remoteAddress = new InetSocketAddress(host, port);
    }

    @Override
    public void connect() {

    }

    @Override
    public int getCompressionThreshold() {
        return DataLoader.getData().getCompressionThreshold();
    }

    @Override
    public void setCompressionThreshold(int threshold) {
        if (this.channel == null) return;

        if (DataLoader.getData().getCompressionThreshold() >= 0) {
            if (channel.pipeline().get("compression") != null) {
                channel.pipeline().remove("compression");
            }

            channel.pipeline().addBefore("decoder", "compression",
                    new TcpPacketCompression(DataLoader.getData().getCompressionThreshold(), DataLoader.getData().isValidateDecompression()));

        } else {
            if (channel.pipeline().get("compression") != null) {
                channel.pipeline().remove("compression");
            }
        }
    }

    @Override
    public boolean isConnected() {
        return this.channel != null && this.channel.isOpen();
    }

    @Override
    public void send(Packet packet, Runnable run) {
        if (this.channel != null && this.channel.isOpen()) {
            this.channel.writeAndFlush(packet).addListener(future -> {
                packet.release();
                if (run != null) run.run();
            });
        } else {
            log.warn("Failed to send: {}", packet.getClass().getSimpleName());
        }
    }

    @Override
    public void disconnect(Component reason, boolean login) {
        if (host.isEmpty()) log.info("Disconnected: {}", ChatUtil.getContent(reason));
        if (this.channel != null && this.channel.isOpen()) {
            if (this.host.isEmpty() && reason != null) {
                this.channel.write(login ? new ClientboundLoginDisconnectPacket(reason) : new ClientboundDisconnectPacket(reason));
            }
            this.channel.flush().close();
        }
        this.channel = null;
        MinecraftProtocol.playerLeft(this.getProfile(), null);
    }

    public void disconnectSilently(Component reason) {
        if (this.channel != null && this.channel.isOpen()) {
            if (this.host.isEmpty()) {
                this.channel.write(new ClientboundDisconnectPacket(reason));
            }
            this.channel.flush().close();
        }
        this.channel = null;
    }

    public void transfer(String host, int port) {
        this.host = host;
        this.port = port;
        disconnectSilently(Component.translatable("disconnect.transfer"));
    }

    public void setState(ProtocolState state) {
        this.getChannel().attr(PROTOCOL_STATE).set(state);
    }
}
