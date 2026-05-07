package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level;

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
public class ClientboundSoundPacket extends Packet {
    private @NonNull Sound sound;
    private @NonNull SoundCategory category;
    private double x;
    private double y;
    private double z;
    private float volume;
    private float pitch;
    private long seed;

    public ClientboundSoundPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundSoundPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.sound = MinecraftTypes.readById(in, BuiltinSound::from, MinecraftTypes::readSoundEvent);
        this.category = MinecraftTypes.readSoundCategory(in);
        this.x = in.readInt() / 8D;
        this.y = in.readInt() / 8D;
        this.z = in.readInt() / 8D;
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
        out.writeInt((int) (this.x * 8));
        out.writeInt((int) (this.y * 8));
        out.writeInt((int) (this.z * 8));
        out.writeFloat(this.volume);
        out.writeFloat(this.pitch);
        out.writeLong(this.seed);
    }
}
