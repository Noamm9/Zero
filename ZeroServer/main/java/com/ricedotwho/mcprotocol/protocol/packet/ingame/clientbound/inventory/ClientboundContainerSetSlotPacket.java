package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.inventory;

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
public class ClientboundContainerSetSlotPacket extends Packet {
    private int containerId;
    private int stateId;
    private int slot;
    private @Nullable ItemStack item;

    public ClientboundContainerSetSlotPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundContainerSetSlotPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.containerId = MinecraftTypes.readVarInt(in);
        this.stateId = MinecraftTypes.readVarInt(in);
        this.slot = in.readShort();
        this.item = MinecraftTypes.readOptionalItemStack(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.containerId);
        MinecraftTypes.writeVarInt(out, this.stateId);
        out.writeShort(this.slot);
        MinecraftTypes.writeOptionalItemStack(out, this.item);
    }
}
