package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level.border;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundSetBorderCenterPacket extends Packet {
    private double newCenterX;
    private double newCenterZ;

    public ClientboundSetBorderCenterPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundSetBorderCenterPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.newCenterX = in.readDouble();
        this.newCenterZ = in.readDouble();
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeDouble(this.newCenterX);
        out.writeDouble(this.newCenterZ);
    }
}
