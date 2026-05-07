package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity.player;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundSetHealthPacket extends Packet {
    private float health;
    private int food;
    private float saturation;

    public ClientboundSetHealthPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundSetHealthPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.health = in.readFloat();
        this.food = MinecraftTypes.readVarInt(in);
        this.saturation = in.readFloat();
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeFloat(this.health);
        MinecraftTypes.writeVarInt(out, this.food);
        out.writeFloat(this.saturation);
    }
}
