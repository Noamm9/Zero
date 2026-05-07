package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundLockDifficultyPacket extends Packet {
    private boolean locked;

    public ServerboundLockDifficultyPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundLockDifficultyPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.locked = in.readBoolean();
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeBoolean(this.locked);
    }
}
