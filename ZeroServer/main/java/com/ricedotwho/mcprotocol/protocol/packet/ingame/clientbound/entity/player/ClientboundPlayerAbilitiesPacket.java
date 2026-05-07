package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity.player;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundPlayerAbilitiesPacket extends Packet {
    private static final int FLAG_INVINCIBLE = 0x01;
    private static final int FLAG_FLYING = 0x02;
    private static final int FLAG_CAN_FLY = 0x04;
    private static final int FLAG_CREATIVE = 0x08;

    private boolean invincible;
    private boolean canFly;
    private boolean flying;
    private boolean creative;
    private float flySpeed;
    private float walkSpeed;

    public ClientboundPlayerAbilitiesPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundPlayerAbilitiesPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        byte flags = in.readByte();
        this.invincible = (flags & FLAG_INVINCIBLE) > 0;
        this.canFly = (flags & FLAG_CAN_FLY) > 0;
        this.flying = (flags & FLAG_FLYING) > 0;
        this.creative = (flags & FLAG_CREATIVE) > 0;

        this.flySpeed = in.readFloat();
        this.walkSpeed = in.readFloat();
    }

    @Override
    public void encode(ByteBuf out) {
        int flags = 0;
        if (this.invincible) {
            flags |= FLAG_INVINCIBLE;
        }

        if (this.canFly) {
            flags |= FLAG_CAN_FLY;
        }

        if (this.flying) {
            flags |= FLAG_FLYING;
        }

        if (this.creative) {
            flags |= FLAG_CREATIVE;
        }

        out.writeByte(flags);

        out.writeFloat(this.flySpeed);
        out.writeFloat(this.walkSpeed);
    }
}
