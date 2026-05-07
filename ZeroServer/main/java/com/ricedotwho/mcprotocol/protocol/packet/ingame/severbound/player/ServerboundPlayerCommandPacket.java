package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.player;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.PlayerState;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundPlayerCommandPacket extends Packet {
    private int entityId;
    private @NonNull PlayerState state;
    private int jumpBoost;

    public ServerboundPlayerCommandPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundPlayerCommandPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.entityId = MinecraftTypes.readVarInt(in);
        this.state = PlayerState.from(MinecraftTypes.readVarInt(in));
        this.jumpBoost = MinecraftTypes.readVarInt(in);

    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.entityId);
        MinecraftTypes.writeVarInt(out, this.state.ordinal());
        MinecraftTypes.writeVarInt(out, this.jumpBoost);
    }
}
