package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundSetTimePacket extends Packet {
    private long gameTime;
    private long dayTime;
    private boolean tickDayTime;

    public ClientboundSetTimePacket(ByteBuf data) {
        super(data);
    }

    public ClientboundSetTimePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.gameTime = in.readLong();
        this.dayTime = in.readLong();
        this.tickDayTime = in.readBoolean();
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeLong(this.gameTime);
        out.writeLong(this.dayTime);
        out.writeBoolean(this.tickDayTime);
    }
}
