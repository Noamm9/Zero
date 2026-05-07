package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudburstmc.math.vector.Vector3d;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.entity.object.*;
import org.geysermc.mcprotocollib.protocol.data.game.entity.type.EntityType;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundAddEntityPacket extends Packet {
    private static final GenericObjectData EMPTY_DATA = new GenericObjectData(0);

    private int entityId;
    private @NonNull UUID uuid;
    private @NonNull EntityType type;
    private @NonNull ObjectData data;
    private double x;
    private double y;
    private double z;
    private Vector3d movement;
    private float yaw;
    private float headYaw;
    private float pitch;

    public ClientboundAddEntityPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundAddEntityPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.entityId = MinecraftTypes.readVarInt(in);
        this.uuid = MinecraftTypes.readUUID(in);
        this.type = EntityType.from(MinecraftTypes.readVarInt(in));
        this.x = in.readDouble();
        this.y = in.readDouble();
        this.z = in.readDouble();
        this.movement = MinecraftTypes.readLpVec3(in);
        this.pitch = in.readByte() * 360 / 256f;
        this.yaw = in.readByte() * 360 / 256f;
        this.headYaw = in.readByte() * 360 / 256f;

        int data = MinecraftTypes.readVarInt(in);
        if (this.type == EntityType.MINECART) {
            this.data = MinecartType.from(data);
        } else if (this.type == EntityType.ITEM_FRAME || this.type == EntityType.GLOW_ITEM_FRAME || this.type == EntityType.PAINTING) {
            this.data = Direction.VALUES[data];
        } else if (this.type == EntityType.FALLING_BLOCK) {
            this.data = new FallingBlockData(data & 65535, data >> 16);
        } else if (this.type.isProjectile()) {
            this.data = new ProjectileData(data);
        } else if (this.type == EntityType.WARDEN) {
            this.data = new WardenData(data);
        } else {
            if (data == 0) {
                this.data = EMPTY_DATA;
            } else {
                this.data = new GenericObjectData(data);
            }
        }
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.entityId);
        MinecraftTypes.writeUUID(out, this.uuid);
        MinecraftTypes.writeVarInt(out, this.type.ordinal());
        out.writeDouble(this.x);
        out.writeDouble(this.y);
        out.writeDouble(this.z);
        MinecraftTypes.writeLpVec3(out, this.movement);
        out.writeByte((byte) (this.pitch * 256 / 360));
        out.writeByte((byte) (this.yaw * 256 / 360));
        out.writeByte((byte) (this.headYaw * 256 / 360));

        int data = 0;
        if (this.data instanceof MinecartType) {
            data = ((MinecartType) this.data).ordinal();
        } else if (this.data instanceof Direction) {
            data = ((Direction) this.data).ordinal();
        } else if (this.data instanceof FallingBlockData) {
            data = ((FallingBlockData) this.data).getId() | ((FallingBlockData) this.data).getMetadata() << 16;
        } else if (this.data instanceof ProjectileData) {
            data = ((ProjectileData) this.data).getOwnerId();
        } else if (this.data instanceof GenericObjectData) {
            data = ((GenericObjectData) this.data).getValue();
        }

        MinecraftTypes.writeVarInt(out, data);
    }
}
