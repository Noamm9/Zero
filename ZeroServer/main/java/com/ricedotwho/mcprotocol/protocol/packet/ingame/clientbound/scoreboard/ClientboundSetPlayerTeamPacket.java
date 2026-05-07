package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.scoreboard;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.scoreboard.CollisionRule;
import org.geysermc.mcprotocollib.protocol.data.game.scoreboard.NameTagVisibility;
import org.geysermc.mcprotocollib.protocol.data.game.scoreboard.TeamAction;
import org.geysermc.mcprotocollib.protocol.data.game.scoreboard.TeamColor;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundSetPlayerTeamPacket extends Packet {
    private @NonNull String teamName;
    private @NonNull TeamAction action;

    private Component displayName;
    private Component prefix;
    private Component suffix;
    private boolean friendlyFire;
    private boolean seeFriendlyInvisibles;
    private @Nullable NameTagVisibility nameTagVisibility;
    private @Nullable CollisionRule collisionRule;
    private TeamColor color;

    private String[] players;

    public ClientboundSetPlayerTeamPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundSetPlayerTeamPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.teamName = MinecraftTypes.readString(in);
        this.action = TeamAction.from(in.readByte());
        if (this.action == TeamAction.CREATE || this.action == TeamAction.UPDATE) {
            this.displayName = MinecraftTypes.readComponent(in);
            byte flags = in.readByte();
            this.friendlyFire = (flags & 0x1) != 0;
            this.seeFriendlyInvisibles = (flags & 0x2) != 0;
            this.nameTagVisibility = NameTagVisibility.from(MinecraftTypes.readVarInt(in));
            this.collisionRule = CollisionRule.from(MinecraftTypes.readVarInt(in));

            this.color = TeamColor.VALUES[MinecraftTypes.readVarInt(in)];

            this.prefix = MinecraftTypes.readComponent(in);
            this.suffix = MinecraftTypes.readComponent(in);
        } else {
            this.displayName = null;
            this.prefix = null;
            this.suffix = null;
            this.friendlyFire = false;
            this.seeFriendlyInvisibles = false;
            this.nameTagVisibility = null;
            this.collisionRule = null;
            this.color = null;
        }

        if (this.action == TeamAction.CREATE || this.action == TeamAction.ADD_PLAYER || this.action == TeamAction.REMOVE_PLAYER) {
            this.players = new String[MinecraftTypes.readVarInt(in)];
            for (int index = 0; index < this.players.length; index++) {
                this.players[index] = MinecraftTypes.readString(in);
            }
        } else {
            this.players = null;
        }
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeString(out, this.teamName);
        out.writeByte(this.action.ordinal());
        if (this.action == TeamAction.CREATE || this.action == TeamAction.UPDATE) {
            MinecraftTypes.writeComponent(out, this.displayName);
            out.writeByte((this.friendlyFire ? 0x1 : 0x0) | (this.seeFriendlyInvisibles ? 0x2 : 0x0));
            MinecraftTypes.writeVarInt(out, this.nameTagVisibility.ordinal());
            MinecraftTypes.writeVarInt(out, this.collisionRule.ordinal());
            MinecraftTypes.writeVarInt(out, this.color.ordinal());
            MinecraftTypes.writeComponent(out, this.prefix);
            MinecraftTypes.writeComponent(out, this.suffix);
        }

        if (this.action == TeamAction.CREATE || this.action == TeamAction.ADD_PLAYER || this.action == TeamAction.REMOVE_PLAYER) {
            MinecraftTypes.writeVarInt(out, this.players.length);
            for (String player : this.players) {
                if (player != null) {
                    MinecraftTypes.writeString(out, player);
                }
            }
        }
    }
}
