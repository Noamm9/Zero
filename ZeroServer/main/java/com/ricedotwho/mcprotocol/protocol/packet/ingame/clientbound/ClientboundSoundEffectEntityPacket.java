package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.level.sound.BuiltinSound;
import org.geysermc.mcprotocollib.protocol.data.game.level.sound.CustomSound;
import org.geysermc.mcprotocollib.protocol.data.game.level.sound.Sound;
import org.geysermc.mcprotocollib.protocol.data.game.level.sound.SoundCategory;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundSoundEffectEntityPacket extends Packet {
    private @NonNull Sound sound;
    private @NonNull SoundCategory category;
    private int entityId;
    private float volume;
    private float pitch;
    private long seed;

    public ClientboundSoundEffectEntityPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundSoundEffectEntityPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.sound = MinecraftTypes.readById(in, BuiltinSound::from, MinecraftTypes::readSoundEvent);
        this.category = MinecraftTypes.readSoundCategory(in);
        this.entityId = MinecraftTypes.readVarInt(in);
        this.volume = in.readFloat();
        this.pitch = in.readFloat();
        this.seed = in.readLong();
    }

    @Override
    public void encode(ByteBuf out) {
        if (this.sound instanceof CustomSound) {
            MinecraftTypes.writeVarInt(out, 0);
            MinecraftTypes.writeSoundEvent(out, this.sound);
        } else {
            MinecraftTypes.writeVarInt(out, ((BuiltinSound) this.sound).ordinal() + 1);
        }
        MinecraftTypes.writeSoundCategory(out, this.category);
        MinecraftTypes.writeVarInt(out, this.entityId);
        out.writeFloat(this.volume);
        out.writeFloat(this.pitch);
        out.writeLong(this.seed);
    }
}
