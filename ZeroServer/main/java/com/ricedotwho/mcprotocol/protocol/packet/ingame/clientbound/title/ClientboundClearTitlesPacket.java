package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.title;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundClearTitlesPacket extends Packet {
    private boolean resetTimes;

    public ClientboundClearTitlesPacket(ByteBuf data) {
        super( data);
    }

    public ClientboundClearTitlesPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.resetTimes = in.readBoolean();
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeBoolean(this.resetTimes);
    }
}
