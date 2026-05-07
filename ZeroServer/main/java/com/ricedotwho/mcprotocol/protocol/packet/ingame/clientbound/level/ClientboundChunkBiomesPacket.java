package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.chunk.ChunkBiomeData;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundChunkBiomesPacket extends Packet {
    private List<ChunkBiomeData> chunkBiomeData;

    public ClientboundChunkBiomesPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundChunkBiomesPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.chunkBiomeData = MinecraftTypes.readList(in, buf -> {
            long raw = buf.readLong();
            return new ChunkBiomeData((int) raw, (int) (raw >> 32), MinecraftTypes.readByteArray(buf));
        });
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeList(out, this.chunkBiomeData, (buf, entry) -> {
            long raw = (long) entry.getX() & 0xFFFFFFFFL | ((long) entry.getZ() & 0xFFFFFFFFL) << 32;
            buf.writeLong(raw);
            MinecraftTypes.writeByteArray(buf, entry.getBuffer());
        });
    }
}
