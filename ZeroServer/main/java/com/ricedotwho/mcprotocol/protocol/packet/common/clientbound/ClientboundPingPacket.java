package com.ricedotwho.mcprotocol.protocol.packet.common.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundPingPacket extends Packet {
    private int pingId;

    public ClientboundPingPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundPingPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.pingId = in.readInt();
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeInt(this.pingId);
    }
}
