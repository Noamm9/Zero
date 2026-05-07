package com.ricedotwho.mcprotocol.protocol.packet.ping.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundPongResponsePacket extends Packet {
    private long pingTime;

    public ClientboundPongResponsePacket(ByteBuf data) {
        super(data);
    }

    public ClientboundPongResponsePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.pingTime = in.readLong();
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeLong(this.pingTime);
    }
}
