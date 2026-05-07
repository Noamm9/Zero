package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.advancement.Advancement;
import org.geysermc.mcprotocollib.protocol.data.game.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundUpdateAdvancementsPacket extends Packet {
    private static final int FLAG_HAS_BACKGROUND_TEXTURE = 0x01;
    private static final int FLAG_SHOW_TOAST = 0x02;
    private static final int FLAG_HIDDEN = 0x04;

    private boolean reset;
    private @NonNull Advancement[] advancements;
    private @NonNull String[] removedAdvancements;
    private @NonNull Map<String, Map<String, Long>> progress;
    private boolean showAdvancements;

    public Map<String, Long> getProgress(@NonNull String advancementId) {
        return this.progress.get(advancementId);
    }

    public ClientboundUpdateAdvancementsPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundUpdateAdvancementsPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.reset = in.readBoolean();

        this.advancements = new Advancement[MinecraftTypes.readVarInt(in)];
        for (int i = 0; i < this.advancements.length; i++) {
            String id = MinecraftTypes.readString(in);
            String parentId = MinecraftTypes.readNullable(in, MinecraftTypes::readString);
            Advancement.DisplayData displayData = MinecraftTypes.readNullable(in, buf -> {
                Component title = MinecraftTypes.readComponent(buf);
                Component description = MinecraftTypes.readComponent(buf);
                ItemStack icon = MinecraftTypes.readOptionalItemStack(buf);
                Advancement.DisplayData.AdvancementType advancementType = Advancement.DisplayData.AdvancementType.from(MinecraftTypes.readVarInt(buf));

                int flags = buf.readInt();
                boolean hasBackgroundTexture = (flags & FLAG_HAS_BACKGROUND_TEXTURE) != 0;
                boolean showToast = (flags & FLAG_SHOW_TOAST) != 0;
                boolean hidden = (flags & FLAG_HIDDEN) != 0;

                String backgroundTexture = hasBackgroundTexture ? MinecraftTypes.readString(buf) : null;
                float posX = buf.readFloat();
                float posY = buf.readFloat();

                return new Advancement.DisplayData(title, description, icon, advancementType, showToast, hidden, posX, posY, backgroundTexture);
            });

            List<List<String>> requirements = MinecraftTypes.readList(in, buf -> MinecraftTypes.readList(buf, MinecraftTypes::readString));

            boolean sendTelemetryEvent = in.readBoolean();

            this.advancements[i] = new Advancement(id, requirements, parentId, displayData, sendTelemetryEvent);
        }

        this.removedAdvancements = new String[MinecraftTypes.readVarInt(in)];
        for (int i = 0; i < this.removedAdvancements.length; i++) {
            this.removedAdvancements[i] = MinecraftTypes.readString(in);
        }

        this.progress = new HashMap<>();
        int progressCount = MinecraftTypes.readVarInt(in);
        for (int i = 0; i < progressCount; i++) {
            String advancementId = MinecraftTypes.readString(in);

            Map<String, Long> advancementProgress = new HashMap<>();
            int criterionCount = MinecraftTypes.readVarInt(in);
            for (int j = 0; j < criterionCount; j++) {
                String criterionId = MinecraftTypes.readString(in);
                long achievedDate = in.readBoolean() ? in.readLong() : -1;
                advancementProgress.put(criterionId, achievedDate);
            }

            this.progress.put(advancementId, advancementProgress);
        }

        this.showAdvancements = in.readBoolean();
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeBoolean(this.reset);

        MinecraftTypes.writeVarInt(out, this.advancements.length);
        for (Advancement advancement : this.advancements) {
            MinecraftTypes.writeString(out, advancement.getId());
            if (advancement.getParentId() != null) {
                out.writeBoolean(true);
                MinecraftTypes.writeString(out, advancement.getParentId());
            } else {
                out.writeBoolean(false);
            }

            MinecraftTypes.writeNullable(out, advancement.getDisplayData(), (buf, data) -> {
                MinecraftTypes.writeComponent(buf, data.getTitle());
                MinecraftTypes.writeComponent(buf, data.getDescription());
                MinecraftTypes.writeOptionalItemStack(buf, data.getIcon());
                MinecraftTypes.writeVarInt(buf, data.getAdvancementType().ordinal());

                int flags = 0;
                if (data.getBackgroundTexture() != null) {
                    flags |= FLAG_HAS_BACKGROUND_TEXTURE;
                }

                if (data.isShowToast()) {
                    flags |= FLAG_SHOW_TOAST;
                }

                if (data.isHidden()) {
                    flags |= FLAG_HIDDEN;
                }

                buf.writeInt(flags);

                if (data.getBackgroundTexture() != null) {
                    MinecraftTypes.writeString(buf, data.getBackgroundTexture());
                }

                buf.writeFloat(data.getPosX());
                buf.writeFloat(data.getPosY());
            });

            MinecraftTypes.writeList(out, advancement.getRequirements(), (buf, requirement) -> MinecraftTypes.writeList(buf, requirement, MinecraftTypes::writeString));

            out.writeBoolean(advancement.isSendsTelemetryEvent());
        }

        MinecraftTypes.writeVarInt(out, this.removedAdvancements.length);
        for (String id : this.removedAdvancements) {
            MinecraftTypes.writeString(out, id);
        }

        MinecraftTypes.writeVarInt(out, this.progress.size());
        for (Map.Entry<String, Map<String, Long>> advancement : this.progress.entrySet()) {
            MinecraftTypes.writeString(out, advancement.getKey());
            Map<String, Long> advancementProgress = advancement.getValue();
            MinecraftTypes.writeVarInt(out, advancementProgress.size());
            for (Map.Entry<String, Long> criterion : advancementProgress.entrySet()) {
                MinecraftTypes.writeString(out, criterion.getKey());
                if (criterion.getValue() != -1) {
                    out.writeBoolean(true);
                    out.writeLong(criterion.getValue());
                } else {
                    out.writeBoolean(false);
                }
            }
        }

        out.writeBoolean(this.showAdvancements);
    }
}
