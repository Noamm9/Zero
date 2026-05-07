package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.player;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundSetCarriedItemPacket extends Packet {
    private int slot;

    public ServerboundSetCarriedItemPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundSetCarriedItemPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.slot = in.readShort();
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeShort(this.slot);
    }
}
