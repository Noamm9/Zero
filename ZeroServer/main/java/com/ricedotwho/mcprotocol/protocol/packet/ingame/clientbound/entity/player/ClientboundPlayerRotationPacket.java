package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity.player;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundPlayerRotationPacket extends Packet {
    private float yRot;
    private boolean relativeY;
    private float xRot;
    private boolean relativeX;

    public ClientboundPlayerRotationPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundPlayerRotationPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.yRot = in.readFloat();
        this.relativeY = in.readBoolean();
        this.xRot = in.readFloat();
        this.relativeX = in.readBoolean();
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeFloat(this.yRot);
        out.writeBoolean(this.relativeY);
        out.writeFloat(this.xRot);
        out.writeBoolean(this.relativeX);
    }
}
