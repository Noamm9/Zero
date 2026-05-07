package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.title;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundSetTitlesAnimationPacket extends Packet {
    private int fadeIn;
    private int stay;
    private int fadeOut;

    public ClientboundSetTitlesAnimationPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundSetTitlesAnimationPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.fadeIn = in.readInt();
        this.stay = in.readInt();
        this.fadeOut = in.readInt();
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeInt(this.fadeIn);
        out.writeInt(this.stay);
        out.writeInt(this.fadeOut);
    }
}
