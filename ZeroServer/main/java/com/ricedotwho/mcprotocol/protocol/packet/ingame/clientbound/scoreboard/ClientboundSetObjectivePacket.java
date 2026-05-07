package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.scoreboard;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.chat.numbers.NumberFormat;
import org.geysermc.mcprotocollib.protocol.data.game.scoreboard.ObjectiveAction;
import org.geysermc.mcprotocollib.protocol.data.game.scoreboard.ScoreType;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundSetObjectivePacket extends Packet {
    private @NonNull String name;
    private @NonNull ObjectiveAction action;


    /**
     * Not null if {@link #getAction()} is {@link ObjectiveAction#ADD} or {@link ObjectiveAction#UPDATE}
     */
    private @Nullable Component displayName;

    /**
     * Not null if {@link #getAction()} is {@link ObjectiveAction#ADD} or {@link ObjectiveAction#UPDATE}
     */
    private @Nullable ScoreType type;
    private @Nullable NumberFormat numberFormat;

    public ClientboundSetObjectivePacket(ByteBuf data) {
        super(data);
    }

    public ClientboundSetObjectivePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.name = MinecraftTypes.readString(in);
        this.action = ObjectiveAction.from(in.readByte());
        if (this.action == ObjectiveAction.ADD || this.action == ObjectiveAction.UPDATE) {
            this.displayName = MinecraftTypes.readComponent(in);
            this.type = ScoreType.from(MinecraftTypes.readVarInt(in));
            this.numberFormat = MinecraftTypes.readNullable(in, MinecraftTypes::readNumberFormat);
        } else {
            this.displayName = null;
            this.type = ScoreType.INTEGER;
            this.numberFormat = null;
        }
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeString(out, this.name);
        out.writeByte(this.action.ordinal());
        if (this.action == ObjectiveAction.ADD || this.action == ObjectiveAction.UPDATE) {
            MinecraftTypes.writeComponent(out, this.displayName);
            MinecraftTypes.writeVarInt(out, this.type.ordinal());
            MinecraftTypes.writeNullable(out, this.numberFormat, MinecraftTypes::writeNumberFormat);
        }
    }
}
