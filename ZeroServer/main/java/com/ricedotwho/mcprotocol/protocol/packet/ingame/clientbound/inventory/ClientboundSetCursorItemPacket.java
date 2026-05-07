package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.inventory;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.item.ItemStack;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundSetCursorItemPacket extends Packet {
    private ItemStack contents;

    public ClientboundSetCursorItemPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundSetCursorItemPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.contents = MinecraftTypes.readOptionalItemStack(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeOptionalItemStack(out, this.contents);
    }
}
