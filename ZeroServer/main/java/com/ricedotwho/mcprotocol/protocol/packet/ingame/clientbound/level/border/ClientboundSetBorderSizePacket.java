package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level.border;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundSetBorderSizePacket extends Packet {
    private double size;

    public ClientboundSetBorderSizePacket(ByteBuf data) {
        super(data);
    }

    public ClientboundSetBorderSizePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.size = in.readDouble();
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeDouble(this.size);
    }
}
