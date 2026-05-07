package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundForgetLevelChunkPacket extends Packet {
    private int x;
    private int z;

    public ClientboundForgetLevelChunkPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundForgetLevelChunkPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        long chunkPosition = in.readLong();
        this.x = (int) chunkPosition;
        this.z = (int) (chunkPosition >> 32);
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeLong(this.x & 0xFFFFFFFFL | (this.z & 0xFFFFFFFFL) << 32);
    }
}
