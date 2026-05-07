package com.ricedotwho.mcprotocol.protocol.net.session;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

@Getter
@Setter
public class ServerSession implements Session {
    private final String host;
    private final int port;
    private SocketAddress localAddress;
    private SocketAddress remoteAddress;

    public ServerSession(String host, int port) {
        this.host = host;
        this.port = port;
        this.localAddress = new InetSocketAddress(host, port);
    }

    @Override
    public void connect() {

    }

    @Override
    public int getCompressionThreshold() {
        return 0;
    }

    @Override
    public void setCompressionThreshold(int threshold) {

    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void send(Packet packet, Runnable run) {

    }

    @Override
    public void disconnect(Component reason, boolean login) {

    }
}
