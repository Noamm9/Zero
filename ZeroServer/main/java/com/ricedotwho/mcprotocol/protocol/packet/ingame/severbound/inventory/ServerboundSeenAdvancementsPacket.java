package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.inventory;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.inventory.AdvancementTabAction;

import java.util.function.Consumer;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundSeenAdvancementsPacket extends Packet {
    private @NonNull AdvancementTabAction action;
    private String tabId;

    public ServerboundSeenAdvancementsPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundSeenAdvancementsPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.action = AdvancementTabAction.from(MinecraftTypes.readVarInt(in));
        this.tabId = switch (this.action) {
            case CLOSED_SCREEN -> null;
            case OPENED_TAB -> MinecraftTypes.readString(in);
        };
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.action.ordinal());
        Consumer<String> tabIdWriter = switch (this.action) {
            case CLOSED_SCREEN -> tabId -> {
            };
            case OPENED_TAB -> tabId -> MinecraftTypes.writeString(out, tabId);
        };
        tabIdWriter.accept(this.tabId);
    }
}
