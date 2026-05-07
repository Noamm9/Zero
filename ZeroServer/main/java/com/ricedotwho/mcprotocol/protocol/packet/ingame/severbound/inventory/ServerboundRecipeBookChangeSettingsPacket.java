package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.inventory;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.inventory.CraftingBookStateType;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundRecipeBookChangeSettingsPacket extends Packet {
    private @NonNull CraftingBookStateType type;
    private boolean bookOpen;
    private boolean filterActive;

    public ServerboundRecipeBookChangeSettingsPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundRecipeBookChangeSettingsPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.type = CraftingBookStateType.from(MinecraftTypes.readVarInt(in));
        this.bookOpen = in.readBoolean();
        this.filterActive = in.readBoolean();
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.type.ordinal());
        out.writeBoolean(this.bookOpen);
        out.writeBoolean(this.filterActive);
    }
}
