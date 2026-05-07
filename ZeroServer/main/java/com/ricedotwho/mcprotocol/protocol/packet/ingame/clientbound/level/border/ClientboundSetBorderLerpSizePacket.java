package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level.border;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundSetBorderLerpSizePacket extends Packet {
    private double oldSize;
    private double newSize;
    private long lerpTime;

    public ClientboundSetBorderLerpSizePacket(ByteBuf data) {
        super(data);
    }

    public ClientboundSetBorderLerpSizePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.oldSize = in.readDouble();
        this.newSize = in.readDouble();
        this.lerpTime = MinecraftTypes.readVarLong(in);
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeDouble(this.oldSize);
        out.writeDouble(this.newSize);
        MinecraftTypes.writeVarLong(out, this.lerpTime);
    }
}
