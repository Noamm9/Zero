package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity.player;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundPlayerCombatKillPacket extends Packet {
    private int playerId;
    private Component message;

    public ClientboundPlayerCombatKillPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundPlayerCombatKillPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.playerId = MinecraftTypes.readVarInt(in);
        this.message = MinecraftTypes.readComponent(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.playerId);
        MinecraftTypes.writeComponent(out, this.message);
    }
}
