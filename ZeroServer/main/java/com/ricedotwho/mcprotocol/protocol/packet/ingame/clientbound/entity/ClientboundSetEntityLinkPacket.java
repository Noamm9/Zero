package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundSetEntityLinkPacket extends Packet {
    private int entityId;
    private int attachedToId;

    public ClientboundSetEntityLinkPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundSetEntityLinkPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.entityId = in.readInt();
        this.attachedToId = in.readInt();
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeInt(this.entityId);
        out.writeInt(this.attachedToId);
    }
}
