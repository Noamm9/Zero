package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.inventory;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.item.ItemStack;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundSetCreativeModeSlotPacket extends Packet {
    private short slot;
    private @Nullable ItemStack clickedItem;

    public ServerboundSetCreativeModeSlotPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundSetCreativeModeSlotPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.slot = in.readShort();
        this.clickedItem = MinecraftTypes.readOptionalItemStack(in, true);
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeShort(this.slot);
        MinecraftTypes.writeOptionalItemStack(out, this.clickedItem, true);
    }
}
