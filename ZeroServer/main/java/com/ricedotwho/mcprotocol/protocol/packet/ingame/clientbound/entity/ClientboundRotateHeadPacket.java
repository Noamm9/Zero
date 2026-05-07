package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundRotateHeadPacket extends Packet {
    private int entityId;
    private float headYaw;

    public ClientboundRotateHeadPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundRotateHeadPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.entityId = MinecraftTypes.readVarInt(in);
        this.headYaw = in.readByte() * 360 / 256f;
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.entityId);
        out.writeByte((byte) (this.headYaw * 256 / 360));
    }
}
