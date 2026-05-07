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
public class ClientboundSetExperiencePacket extends Packet {
    private float experience;
    private int level;
    private int totalExperience;

    public ClientboundSetExperiencePacket(ByteBuf data) {
        super(data);
    }

    public ClientboundSetExperiencePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.experience = in.readFloat();
        this.level = MinecraftTypes.readVarInt(in);
        this.totalExperience = MinecraftTypes.readVarInt(in);
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeFloat(this.experience);
        MinecraftTypes.writeVarInt(out, this.level);
        MinecraftTypes.writeVarInt(out, this.totalExperience);
    }
}
