package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.player;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.GameMode;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundChangeGameModePacket extends Packet {
    private GameMode mode;

    public ServerboundChangeGameModePacket(ByteBuf data) {
        super(data);
    }

    public ServerboundChangeGameModePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.mode = GameMode.byId(MinecraftTypes.readVarInt(in));
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.mode.ordinal());
    }
}
