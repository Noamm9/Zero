package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundTickingStatePacket extends Packet {
    private float tickRate;
    private boolean isFrozen;

    public ClientboundTickingStatePacket(ByteBuf data) {
        super(data);
    }

    public ClientboundTickingStatePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.tickRate = in.readFloat();
        this.isFrozen = in.readBoolean();
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeFloat(tickRate);
        out.writeBoolean(isFrozen);
    }
}
