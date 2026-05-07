package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.level.sound.SoundCategory;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundStopSoundPacket extends Packet {
    private static final int FLAG_CATEGORY = 0x01;
    private static final int FLAG_SOUND = 0x02;

    private @Nullable SoundCategory category;
    private @Nullable Key sound;

    public ClientboundStopSoundPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundStopSoundPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        int flags = in.readByte();
        if ((flags & FLAG_CATEGORY) != 0) {
            this.category = MinecraftTypes.readSoundCategory(in);
        } else {
            this.category = null;
        }

        if ((flags & FLAG_SOUND) != 0) {
            this.sound = MinecraftTypes.readResourceLocation(in);
        } else {
            this.sound = null;
        }
    }

    @Override
    public void encode(ByteBuf out) {
        int flags = 0;
        if (this.category != null) {
            flags |= FLAG_CATEGORY;
        }

        if (this.sound != null) {
            flags |= FLAG_SOUND;
        }

        out.writeByte(flags);
        if (this.category != null) {
            out.writeByte(this.category.ordinal());
        }

        if (this.sound != null) {
            MinecraftTypes.writeResourceLocation(out, this.sound);
        }
    }
}
