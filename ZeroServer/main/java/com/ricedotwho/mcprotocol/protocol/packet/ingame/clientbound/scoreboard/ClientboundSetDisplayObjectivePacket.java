package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.scoreboard;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.scoreboard.ScoreboardPosition;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundSetDisplayObjectivePacket extends Packet {
    private @NonNull ScoreboardPosition position;
    private @NonNull String name;

    public ClientboundSetDisplayObjectivePacket(ByteBuf data) {
        super(data);
    }

    public ClientboundSetDisplayObjectivePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.position = ScoreboardPosition.from(MinecraftTypes.readVarInt(in));
        this.name = MinecraftTypes.readString(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.position.ordinal());
        MinecraftTypes.writeString(out, this.name);
    }
}
