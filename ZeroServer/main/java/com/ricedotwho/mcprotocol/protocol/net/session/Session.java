package com.ricedotwho.mcprotocol.protocol.net.session;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.net.SocketAddress;
import java.util.List;
import java.util.Map;

public interface Session {
    void connect();

    String getHost();

    int getPort();

    SocketAddress getLocalAddress();

    SocketAddress getRemoteAddress();

    int getCompressionThreshold();

    void setCompressionThreshold(int threshold);

    boolean isConnected();

    default void send(Packet packet) {
        send(packet, null);
    }

    void send(Packet packet, Runnable run);

    default void disconnect(Component reason) {
        disconnect(reason, false);
    }

    void disconnect(Component reason, boolean login);
}
