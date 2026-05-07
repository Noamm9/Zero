package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.entity.metadata.EntityMetadata;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundSetEntityDataPacket extends Packet {
    private int entityId;
    private @NonNull EntityMetadata<?, ?>[] metadata;

    public ClientboundSetEntityDataPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundSetEntityDataPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.entityId = MinecraftTypes.readVarInt(in);
        this.metadata = MinecraftTypes.readEntityMetadata(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.entityId);
        MinecraftTypes.writeEntityMetadata(out, this.metadata);
    }
}
