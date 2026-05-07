package com.ricedotwho.mcprotocol.protocol.net.registry;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

@FunctionalInterface
public interface PacketFactory<T extends Packet> {

    T construct(ByteBuf packet);
}