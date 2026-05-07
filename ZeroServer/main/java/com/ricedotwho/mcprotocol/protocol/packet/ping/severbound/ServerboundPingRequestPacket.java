package com.ricedotwho.mcprotocol.protocol.packet.ping.severbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundPingRequestPacket extends Packet {
    private long pingTime;

    public ServerboundPingRequestPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundPingRequestPacket(Packet packet) {
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
