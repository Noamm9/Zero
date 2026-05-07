package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.math.vector.Vector3d;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundDamageEventPacket extends Packet {
    private int entityId;
    private int sourceTypeId;
    private int sourceCauseId;
    private int sourceDirectId;
    private @Nullable Vector3d sourcePosition;

    public ClientboundDamageEventPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundDamageEventPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.entityId = MinecraftTypes.readVarInt(in);
        this.sourceTypeId = MinecraftTypes.readVarInt(in);
        this.sourceCauseId = MinecraftTypes.readVarInt(in) - 1;
        this.sourceDirectId = MinecraftTypes.readVarInt(in) - 1;
        this.sourcePosition = in.readBoolean() ? Vector3d.from(in.readDouble(), in.readDouble(), in.readDouble()) : null;
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.entityId);
        MinecraftTypes.writeVarInt(out, this.sourceTypeId);
        MinecraftTypes.writeVarInt(out, this.sourceCauseId + 1);
        MinecraftTypes.writeVarInt(out, this.sourceDirectId + 1);

        if (this.sourcePosition != null) {
            out.writeBoolean(true);
            out.writeDouble(this.sourcePosition.getX());
            out.writeDouble(this.sourcePosition.getY());
            out.writeDouble(this.sourcePosition.getZ());
        } else {
            out.writeBoolean(false);
        }
    }
}
