package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.player;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundMovePlayerRotPacket extends Packet {
    private boolean onGround;
    private boolean horizontalCollision;
    private float yaw;
    private float pitch;

    public ServerboundMovePlayerRotPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundMovePlayerRotPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.yaw = in.readFloat();
        this.pitch = in.readFloat();
        int flags = in.readUnsignedByte();
        this.onGround = (flags & 0x1) != 0;
        this.horizontalCollision = (flags & 0x2) != 0;
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeFloat(this.yaw);
        out.writeFloat(this.pitch);
        int flags = 0;
        if (this.onGround) {
            flags |= 0x1;
        }

        if (this.horizontalCollision) {
            flags |= 0x2;
        }

        out.writeByte(flags);
    }
}
