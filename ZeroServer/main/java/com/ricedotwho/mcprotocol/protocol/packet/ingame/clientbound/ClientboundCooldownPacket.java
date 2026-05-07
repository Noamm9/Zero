package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundCooldownPacket extends Packet {
    private Key cooldownGroup;
    private int cooldownTicks;

    public ClientboundCooldownPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundCooldownPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.cooldownGroup = MinecraftTypes.readResourceLocation(in);
        this.cooldownTicks = MinecraftTypes.readVarInt(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeResourceLocation(out, this.cooldownGroup);
        MinecraftTypes.writeVarInt(out, this.cooldownTicks);
    }
}
