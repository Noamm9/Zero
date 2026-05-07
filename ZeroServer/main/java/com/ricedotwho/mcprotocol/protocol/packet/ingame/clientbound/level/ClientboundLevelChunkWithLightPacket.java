package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudburstmc.nbt.NbtMap;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.level.HeightmapTypes;
import org.geysermc.mcprotocollib.protocol.data.game.level.LightUpdateData;
import org.geysermc.mcprotocollib.protocol.data.game.level.block.BlockEntityInfo;
import org.geysermc.mcprotocollib.protocol.data.game.level.block.BlockEntityType;

import java.util.EnumMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundLevelChunkWithLightPacket extends Packet {
    private int x;
    private int z;
    private byte @NonNull [] chunkData;
    private @NonNull Map<HeightmapTypes, long[]> heightMaps;
    private @NonNull BlockEntityInfo @NonNull [] blockEntities;
    private @NonNull LightUpdateData lightData;

    public ClientboundLevelChunkWithLightPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundLevelChunkWithLightPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.x = in.readInt();
        this.z = in.readInt();

        this.heightMaps = new EnumMap<>(HeightmapTypes.class);
        int length = MinecraftTypes.readVarInt(in);
        for (int i = 0; i < length; i++) {
            this.heightMaps.put(HeightmapTypes.from(MinecraftTypes.readVarInt(in)), MinecraftTypes.readLongArray(in));
        }

        this.chunkData = MinecraftTypes.readByteArray(in);

        this.blockEntities = new BlockEntityInfo[MinecraftTypes.readVarInt(in)];
        for (int i = 0; i < this.blockEntities.length; i++) {
            byte xz = in.readByte();
            int blockEntityX = (xz >> 4) & 15;
            int blockEntityZ = xz & 15;
            int blockEntityY = in.readShort();
            BlockEntityType type = MinecraftTypes.readBlockEntityType(in);
            NbtMap tag = MinecraftTypes.readCompoundTag(in);
            this.blockEntities[i] = new BlockEntityInfo(blockEntityX, blockEntityY, blockEntityZ, type, tag);
        }

        this.lightData = MinecraftTypes.readLightUpdateData(in);
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeInt(this.x);
        out.writeInt(this.z);

        MinecraftTypes.writeVarInt(out, this.heightMaps.size());
        for (Map.Entry<HeightmapTypes, long[]> entry : this.heightMaps.entrySet()) {
            MinecraftTypes.writeVarInt(out, entry.getKey().ordinal());
            MinecraftTypes.writeLongArray(out, entry.getValue());
        }

        MinecraftTypes.writeVarInt(out, this.chunkData.length);
        out.writeBytes(this.chunkData);

        MinecraftTypes.writeVarInt(out, this.blockEntities.length);
        for (BlockEntityInfo blockEntity : this.blockEntities) {
            out.writeByte(((blockEntity.getX() & 15) << 4) | blockEntity.getZ() & 15);
            out.writeShort(blockEntity.getY());
            MinecraftTypes.writeBlockEntityType(out, blockEntity.getType());
            MinecraftTypes.writeAnyTag(out, blockEntity.getNbt());
        }

        MinecraftTypes.writeLightUpdateData(out, this.lightData);
    }
}
