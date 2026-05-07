package com.ricedotwho.mcprotocol.protocol.packet.login.severbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundLoginAcknowledgedPacket extends Packet {
    public ServerboundLoginAcknowledgedPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundLoginAcknowledgedPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {

    }

    @Override
    public void encode(ByteBuf out) {

    }
}
