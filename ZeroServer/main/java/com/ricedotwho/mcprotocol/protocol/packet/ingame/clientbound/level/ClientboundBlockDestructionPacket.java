package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudburstmc.math.vector.Vector3i;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.BlockBreakStage;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundBlockDestructionPacket extends Packet {
    private int breakerEntityId;
    private @NonNull Vector3i position;
    private @NonNull BlockBreakStage stage;

    public ClientboundBlockDestructionPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundBlockDestructionPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.breakerEntityId = MinecraftTypes.readVarInt(in);
        this.position = MinecraftTypes.readPosition(in);
        this.stage = MinecraftTypes.readBlockBreakStage(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.breakerEntityId);
        MinecraftTypes.writePosition(out, this.position);
        MinecraftTypes.writeBlockBreakStage(out, this.stage);
    }
}
