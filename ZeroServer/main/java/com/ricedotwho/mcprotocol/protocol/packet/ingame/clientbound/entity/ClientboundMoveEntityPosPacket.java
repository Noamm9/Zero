package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundMoveEntityPosPacket extends Packet {
    private int entityId;
    private double moveX;
    private double moveY;
    private double moveZ;
    private boolean onGround;

    public ClientboundMoveEntityPosPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundMoveEntityPosPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.entityId = MinecraftTypes.readVarInt(in);
        this.moveX = in.readShort() / 4096D;
        this.moveY = in.readShort() / 4096D;
        this.moveZ = in.readShort() / 4096D;
        this.onGround = in.readBoolean();
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.entityId);
        out.writeShort((int) (this.moveX * 4096));
        out.writeShort((int) (this.moveY * 4096));
        out.writeShort((int) (this.moveZ * 4096));
        out.writeBoolean(this.onGround);
    }
}
