package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.inventory;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundSelectBundleItemPacket extends Packet {
    private int slotId;
    private int selectedItemIndex;

    public ServerboundSelectBundleItemPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundSelectBundleItemPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.slotId = MinecraftTypes.readVarInt(in);
        this.selectedItemIndex = MinecraftTypes.readVarInt(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.slotId);
        MinecraftTypes.writeVarInt(out, this.selectedItemIndex);
    }
}
