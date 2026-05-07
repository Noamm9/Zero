package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundRemoveEntitiesPacket extends Packet {
    private int @NonNull [] entityIds;

    public ClientboundRemoveEntitiesPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundRemoveEntitiesPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.entityIds = new int[MinecraftTypes.readVarInt(in)];
        for (int i = 0; i < this.entityIds.length; i++) {
            this.entityIds[i] = MinecraftTypes.readVarInt(in);
        }
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.entityIds.length);
        for (int entityId : this.entityIds) {
            MinecraftTypes.writeVarInt(out, entityId);
        }
    }
}
