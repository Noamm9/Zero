package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudburstmc.math.vector.Vector3i;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.entity.object.Direction;
import org.geysermc.mcprotocollib.protocol.data.game.level.event.*;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundLevelEventPacket extends Packet {
    private @NonNull LevelEvent event;
    private @NonNull Vector3i position;
    private @NonNull LevelEventData data;
    private boolean broadcast;

    public ClientboundLevelEventPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundLevelEventPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.event = MinecraftTypes.readLevelEvent(in);
        this.position = MinecraftTypes.readPosition(in);
        int value = in.readInt();
        if (this.event instanceof LevelEventType levelEventType) {
            switch (levelEventType) {
                case SOUND_EXTINGUISH_FIRE -> this.data = FireExtinguishData.from(value);
                case SOUND_PLAY_JUKEBOX_SONG -> this.data = new RecordEventData(value);
                case PARTICLES_SHOOT_SMOKE, PARTICLES_SHOOT_WHITE_SMOKE -> this.data = new SmokeEventData(Direction.from(Math.abs(value % 6)));
                case PARTICLES_DESTROY_BLOCK, PARTICLES_AND_SOUND_BRUSH_BLOCK_COMPLETE -> this.data = new BreakBlockEventData(value);
                case PARTICLES_SPELL_POTION_SPLASH, PARTICLES_INSTANT_POTION_SPLASH -> this.data = new BreakPotionEventData(value);
                case PARTICLES_AND_SOUND_PLANT_GROWTH -> this.data = new BonemealGrowEventData(value);
                case COMPOSTER_FILL -> this.data = value > 0 ? ComposterEventData.FILL_SUCCESS : ComposterEventData.FILL;
                case PARTICLES_DRAGON_FIREBALL_SPLASH -> this.data = value == 1 ? DragonFireballEventData.HAS_SOUND : DragonFireballEventData.NO_SOUND;
                case PARTICLES_ELECTRIC_SPARK -> this.data = value >= 0 && value < 6 ? new ElectricSparkData(Direction.from(value)) : new UnknownLevelEventData(value);
                case PARTICLES_SCULK_CHARGE -> this.data = new SculkBlockChargeEventData(value);
                case PARTICLES_TRIAL_SPAWNER_DETECT_PLAYER -> this.data = new TrialSpawnerDetectEventData(value);
                default -> this.data = new UnknownLevelEventData(value);
            }
        } else {
            this.data = new UnknownLevelEventData(value);
        }

        this.broadcast = in.readBoolean();
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeLevelEvent(out, this.event);
        MinecraftTypes.writePosition(out, this.position);
        int value;
        if (this.data instanceof FireExtinguishData) {
            value = ((FireExtinguishData) this.data).ordinal();
        } else if (this.data instanceof RecordEventData) {
            value = ((RecordEventData) this.data).getRecordId();
        } else if (this.data instanceof SmokeEventData) {
            value = ((SmokeEventData) this.data).getDirection().ordinal();
        } else if (this.data instanceof BreakBlockEventData) {
            value = ((BreakBlockEventData) this.data).getBlockState();
        } else if (this.data instanceof BreakPotionEventData) {
            value = ((BreakPotionEventData) this.data).getPotionId();
        } else if (this.data instanceof BonemealGrowEventData) {
            value = ((BonemealGrowEventData) this.data).getParticleCount();
        } else if (this.data instanceof ComposterEventData) {
            value = ((ComposterEventData) this.data).ordinal();
        } else if (this.data instanceof DragonFireballEventData) {
            value = ((DragonFireballEventData) this.data).ordinal();
        } else if (this.data instanceof ElectricSparkData) {
            value = ((ElectricSparkData) this.data).getDirection().ordinal();
        } else if (this.data instanceof SculkBlockChargeEventData) {
            value = ((SculkBlockChargeEventData) data).getLevelValue();
        } else {
            value = ((UnknownLevelEventData) data).getData();
        }

        out.writeInt(value);
        out.writeBoolean(this.broadcast);
    }
}
