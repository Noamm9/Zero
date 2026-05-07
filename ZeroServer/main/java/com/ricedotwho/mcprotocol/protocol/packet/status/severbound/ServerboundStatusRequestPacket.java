package com.ricedotwho.mcprotocol.protocol.packet.status.severbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundStatusRequestPacket extends Packet {
    public ServerboundStatusRequestPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundStatusRequestPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {

    }

    @Override
    public void encode(ByteBuf out) {

    }
}
