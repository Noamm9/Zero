package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.inventory;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.item.ItemStack;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundContainerSetContentPacket extends Packet {
    private int containerId;
    private int stateId;
    private @Nullable ItemStack @NonNull [] items;
    private @Nullable ItemStack carriedItem;

    public ClientboundContainerSetContentPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundContainerSetContentPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.containerId = MinecraftTypes.readVarInt(in);
        this.stateId = MinecraftTypes.readVarInt(in);
        this.items = new ItemStack[MinecraftTypes.readVarInt(in)];
        for (int index = 0; index < this.items.length; index++) {
            this.items[index] = MinecraftTypes.readOptionalItemStack(in);
        }
        this.carriedItem = MinecraftTypes.readOptionalItemStack(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.containerId);
        MinecraftTypes.writeVarInt(out, this.stateId);
        MinecraftTypes.writeVarInt(out, this.items.length);
        for (ItemStack item : this.items) {
            MinecraftTypes.writeOptionalItemStack(out, item);
        }
        MinecraftTypes.writeOptionalItemStack(out, this.carriedItem);
    }
}
