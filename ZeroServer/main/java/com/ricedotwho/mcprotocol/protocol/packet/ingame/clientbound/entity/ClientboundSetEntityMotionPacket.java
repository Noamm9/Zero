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
public class ClientboundSetEntityMotionPacket extends Packet {
    private int entityId;
    private Vector3d movement;

    public ClientboundSetEntityMotionPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundSetEntityMotionPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.entityId = MinecraftTypes.readVarInt(in);
        this.movement = MinecraftTypes.readLpVec3(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.entityId);
        MinecraftTypes.writeLpVec3(out, this.movement);
    }
}
