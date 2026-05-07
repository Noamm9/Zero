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
public class ClientboundHurtAnimationPacket extends Packet {
    private int entityId;
    private float yaw;

    public ClientboundHurtAnimationPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundHurtAnimationPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.entityId = MinecraftTypes.readVarInt(in);
        this.yaw = in.readFloat();
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.entityId);
        out.writeFloat(this.yaw);
    }
}
