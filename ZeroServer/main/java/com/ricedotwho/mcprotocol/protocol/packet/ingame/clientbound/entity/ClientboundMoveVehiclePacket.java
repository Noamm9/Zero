package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.math.vector.Vector3d;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundMoveVehiclePacket extends Packet {
    private Vector3d position;
    private float yRot;
    private float xRot;

    public ClientboundMoveVehiclePacket(ByteBuf data) {
        super(data);
    }

    public ClientboundMoveVehiclePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.position = Vector3d.from(in.readDouble(), in.readDouble(), in.readDouble());
        this.yRot = in.readFloat();
        this.xRot = in.readFloat();
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeDouble(this.position.getX());
        out.writeDouble(this.position.getY());
        out.writeDouble(this.position.getZ());
        out.writeFloat(this.yRot);
        out.writeFloat(this.xRot);
    }
}
