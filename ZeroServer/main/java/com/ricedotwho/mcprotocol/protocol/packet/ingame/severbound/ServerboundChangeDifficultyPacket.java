package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.setting.Difficulty;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundChangeDifficultyPacket extends Packet {
    private @NonNull Difficulty difficulty;

    public ServerboundChangeDifficultyPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundChangeDifficultyPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.difficulty = Difficulty.from(MinecraftTypes.readVarInt(in));
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.difficulty.ordinal());
    }
}
