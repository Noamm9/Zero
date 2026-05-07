package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.BossBarAction;
import org.geysermc.mcprotocollib.protocol.data.game.BossBarColor;
import org.geysermc.mcprotocollib.protocol.data.game.BossBarDivision;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundBossEventPacket extends Packet {
    private @NonNull UUID uuid;
    private @NonNull BossBarAction action;

    private Component title;

    private float health;

    private BossBarColor color;
    private BossBarDivision division;

    private boolean darkenSky;
    private boolean playEndMusic;
    private boolean showFog;

    public ClientboundBossEventPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundBossEventPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.uuid = MinecraftTypes.readUUID(in);
        this.action = BossBarAction.from(MinecraftTypes.readVarInt(in));

        if (this.action == BossBarAction.ADD || this.action == BossBarAction.UPDATE_TITLE) {
            this.title = MinecraftTypes.readComponent(in);
        } else {
            this.title = null;
        }

        if (this.action == BossBarAction.ADD || this.action == BossBarAction.UPDATE_HEALTH) {
            this.health = in.readFloat();
        } else {
            this.health = 0f;
        }

        if (this.action == BossBarAction.ADD || this.action == BossBarAction.UPDATE_STYLE) {
            this.color = BossBarColor.from(MinecraftTypes.readVarInt(in));
            this.division = BossBarDivision.from(MinecraftTypes.readVarInt(in));
        } else {
            this.color = null;
            this.division = null;
        }

        if (this.action == BossBarAction.ADD || this.action == BossBarAction.UPDATE_FLAGS) {
            int flags = in.readUnsignedByte();
            this.darkenSky = (flags & 0x1) == 0x1;
            this.playEndMusic = (flags & 0x2) == 0x2;
            this.showFog = (flags & 0x4) == 0x4;
        } else {
            this.darkenSky = false;
            this.playEndMusic = false;
            this.showFog = false;
        }
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeUUID(out, this.uuid);
        MinecraftTypes.writeVarInt(out, this.action.ordinal());

        if (this.action == BossBarAction.ADD || this.action == BossBarAction.UPDATE_TITLE) {
            MinecraftTypes.writeComponent(out, this.title);
        }

        if (this.action == BossBarAction.ADD || this.action == BossBarAction.UPDATE_HEALTH) {
            out.writeFloat(this.health);
        }

        if (this.action == BossBarAction.ADD || this.action == BossBarAction.UPDATE_STYLE) {
            MinecraftTypes.writeVarInt(out, this.color.ordinal());
            MinecraftTypes.writeVarInt(out, this.division.ordinal());
        }

        if (this.action == BossBarAction.ADD || this.action == BossBarAction.UPDATE_FLAGS) {
            int flags = 0;
            if (this.darkenSky) {
                flags |= 0x1;
            }

            if (this.playEndMusic) {
                flags |= 0x2;
            }

            if (this.showFog) {
                flags |= 0x4;
            }

            out.writeByte(flags);
        }
    }
}
