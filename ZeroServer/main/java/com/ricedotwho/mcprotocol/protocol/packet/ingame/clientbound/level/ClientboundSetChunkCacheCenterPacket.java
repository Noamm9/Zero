package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundSetChunkCacheCenterPacket extends Packet {
    private int chunkX;
    private int chunkZ;

    public ClientboundSetChunkCacheCenterPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundSetChunkCacheCenterPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.chunkX = MinecraftTypes.readVarInt(in);
        this.chunkZ = MinecraftTypes.readVarInt(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.chunkX);
        MinecraftTypes.writeVarInt(out, this.chunkZ);
    }
}
