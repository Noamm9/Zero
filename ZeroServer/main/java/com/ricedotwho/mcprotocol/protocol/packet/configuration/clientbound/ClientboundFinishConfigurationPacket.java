package com.ricedotwho.mcprotocol.protocol.packet.configuration.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundFinishConfigurationPacket extends Packet {

    public ClientboundFinishConfigurationPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundFinishConfigurationPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {

    }

    @Override
    public void encode(ByteBuf out) {

    }
}
