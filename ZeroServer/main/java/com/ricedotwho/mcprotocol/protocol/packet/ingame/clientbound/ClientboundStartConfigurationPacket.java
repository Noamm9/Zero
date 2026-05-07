package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundStartConfigurationPacket extends Packet {

    public ClientboundStartConfigurationPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundStartConfigurationPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {

    }

    @Override
    public void encode(ByteBuf out) {

    }
}
