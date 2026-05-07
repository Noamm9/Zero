package com.ricedotwho.mcprotocol.protocol.packet.common.severbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundKeepAlivePacket extends Packet {
    private long pingId;

    public ServerboundKeepAlivePacket(ByteBuf data) {
        super(data);
    }

    public ServerboundKeepAlivePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.pingId = in.readLong();
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeLong(this.pingId);
    }
}
