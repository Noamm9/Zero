package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.setting.Difficulty;

@Getter
@Setter
public class ClientboundChangeDifficultyPacket extends Packet {
    private @NonNull Difficulty difficulty;
    private boolean difficultyLocked;

    public ClientboundChangeDifficultyPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundChangeDifficultyPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.difficulty = Difficulty.from(MinecraftTypes.readVarInt(in));
        this.difficultyLocked = in.readBoolean();
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.difficulty.ordinal());
        out.writeBoolean(this.difficultyLocked);
    }
}
