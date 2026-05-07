package com.ricedotwho.mcprotocol.protocol.net.registry;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

public record PacketDefinition<T extends Packet>(int id, Class<T> packetClass, PacketFactory<T> constructor) {

    public T newInstance(ByteBuf buf) {
        return this.constructor.construct(buf);
    }
}