package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.math.vector.Vector3d;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundEntityPositionSyncPacket extends Packet {
    private int entityId;
    private Vector3d position;
    private Vector3d deltaMovement;
    private float yRot;
    private float xRot;
    private boolean onGround;

    public ClientboundEntityPositionSyncPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundEntityPositionSyncPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.entityId = MinecraftTypes.readVarInt(in);
        this.position = Vector3d.from(in.readDouble(), in.readDouble(), in.readDouble());
        this.deltaMovement = Vector3d.from(in.readDouble(), in.readDouble(), in.readDouble());
        this.yRot = in.readFloat();
        this.xRot = in.readFloat();
        this.onGround = in.readBoolean();
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.entityId);
        out.writeDouble(this.position.getX());
        out.writeDouble(this.position.getY());
        out.writeDouble(this.position.getZ());
        out.writeDouble(this.deltaMovement.getX());
        out.writeDouble(this.deltaMovement.getY());
        out.writeDouble(this.deltaMovement.getZ());
        out.writeFloat(this.yRot);
        out.writeFloat(this.xRot);
        out.writeBoolean(this.onGround);
    }
}
