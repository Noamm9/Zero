package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.nbt.NbtMap;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.level.block.BlockEntityType;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundBlockEntityDataPacket extends Packet {
    private @NonNull Vector3i position;
    private @NonNull BlockEntityType type;
    private @Nullable NbtMap nbt;

    public ClientboundBlockEntityDataPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundBlockEntityDataPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.position = MinecraftTypes.readPosition(in);
        this.type = Objects.requireNonNull(MinecraftTypes.readBlockEntityType(in));
        this.nbt = MinecraftTypes.readCompoundTag(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writePosition(out, this.position);
        MinecraftTypes.writeBlockEntityType(out, this.type);
        MinecraftTypes.writeAnyTag(out, this.nbt);
    }
}
