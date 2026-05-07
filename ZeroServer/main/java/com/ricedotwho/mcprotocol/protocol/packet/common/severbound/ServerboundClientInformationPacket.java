package com.ricedotwho.mcprotocol.protocol.packet.common.severbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.HandPreference;
import org.geysermc.mcprotocollib.protocol.data.game.setting.ChatVisibility;
import org.geysermc.mcprotocollib.protocol.data.game.setting.ParticleStatus;
import org.geysermc.mcprotocollib.protocol.data.game.setting.SkinPart;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundClientInformationPacket extends Packet {
    private @NonNull String locale;
    private int renderDistance;
    private @NonNull ChatVisibility chatVisibility;
    private boolean useChatColors;
    private @NonNull List<SkinPart> visibleParts;
    private @NonNull HandPreference mainHand;
    private boolean textFilteringEnabled;

    private boolean allowsListing;
    private @NonNull ParticleStatus particleStatus;

    public ServerboundClientInformationPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundClientInformationPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.locale = MinecraftTypes.readString(in);
        this.renderDistance = in.readByte();
        this.chatVisibility = ChatVisibility.from(MinecraftTypes.readVarInt(in));
        this.useChatColors = in.readBoolean();
        this.visibleParts = new ArrayList<>();

        int flags = in.readUnsignedByte();
        for (SkinPart part : SkinPart.VALUES) {
            int bit = 1 << part.ordinal();
            if ((flags & bit) == bit) {
                this.visibleParts.add(part);
            }
        }

        this.mainHand = HandPreference.from(MinecraftTypes.readVarInt(in));
        this.textFilteringEnabled = in.readBoolean();
        this.allowsListing = in.readBoolean();
        this.particleStatus = ParticleStatus.from(MinecraftTypes.readVarInt(in));
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeString(out, this.locale);
        out.writeByte(this.renderDistance);
        MinecraftTypes.writeVarInt(out, this.chatVisibility.ordinal());
        out.writeBoolean(this.useChatColors);

        int flags = 0;
        for (SkinPart part : this.visibleParts) {
            flags |= 1 << part.ordinal();
        }

        out.writeByte(flags);

        MinecraftTypes.writeVarInt(out, this.mainHand.ordinal());
        out.writeBoolean(this.textFilteringEnabled);
        out.writeBoolean(allowsListing);
        MinecraftTypes.writeVarInt(out, this.particleStatus.ordinal());
    }
}
