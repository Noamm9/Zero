package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.level;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundChunkBatchReceivedPacket extends Packet {
    private float desiredChunksPerTick;

    public ServerboundChunkBatchReceivedPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundChunkBatchReceivedPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.desiredChunksPerTick = in.readFloat();
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeFloat(this.desiredChunksPerTick);
    }
}
