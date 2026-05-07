package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.player;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundPlayerAbilitiesPacket extends Packet {
    private static final int FLAG_FLYING = 0x02;

    private boolean flying;

    public ServerboundPlayerAbilitiesPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundPlayerAbilitiesPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        byte flags = in.readByte();
        this.flying = (flags & FLAG_FLYING) > 0;
    }

    @Override
    public void encode(ByteBuf out) {
        int flags = 0;

        if (this.flying) {
            flags |= FLAG_FLYING;
        }

        out.writeByte(flags);
    }
}
