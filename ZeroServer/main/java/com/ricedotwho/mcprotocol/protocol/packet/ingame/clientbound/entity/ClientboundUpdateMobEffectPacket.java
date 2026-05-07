package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.entity.Effect;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundUpdateMobEffectPacket extends Packet {
    private static final int FLAG_AMBIENT = 0x01;
    private static final int FLAG_SHOW_PARTICLES = 0x02;
    private static final int FLAG_SHOW_ICON = 0x04;
    private static final int FLAG_BLEND = 0x08;

    private int entityId;
    private @NonNull Effect effect;
    private int amplifier;
    private int duration;
    private boolean ambient;
    private boolean showParticles;
    private boolean showIcon;
    private boolean blend;

    public ClientboundUpdateMobEffectPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundUpdateMobEffectPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.entityId = MinecraftTypes.readVarInt(in);
        this.effect = MinecraftTypes.readEffect(in);
        this.amplifier = MinecraftTypes.readVarInt(in);
        this.duration = MinecraftTypes.readVarInt(in);

        int flags = in.readByte();
        this.ambient = (flags & FLAG_AMBIENT) != 0;
        this.showParticles = (flags & FLAG_SHOW_PARTICLES) != 0;
        this.showIcon = (flags & FLAG_SHOW_ICON) != 0;
        this.blend = (flags & FLAG_BLEND) != 0;
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.entityId);
        MinecraftTypes.writeEffect(out, this.effect);
        MinecraftTypes.writeVarInt(out, this.amplifier);
        MinecraftTypes.writeVarInt(out, this.duration);

        int flags = 0;
        if (this.ambient) {
            flags |= FLAG_AMBIENT;
        }
        if (this.showParticles) {
            flags |= FLAG_SHOW_PARTICLES;
        }
        if (this.showIcon) {
            flags |= FLAG_SHOW_ICON;
        }
        if (this.blend) {
            flags |= FLAG_BLEND;
        }

        out.writeByte(flags);
    }
}
