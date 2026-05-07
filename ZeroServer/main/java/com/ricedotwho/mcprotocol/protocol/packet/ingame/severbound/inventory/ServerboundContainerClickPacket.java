package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.inventory;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import com.ricedotwho.zero.Zero;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.inventory.*;
import org.geysermc.mcprotocollib.protocol.data.game.item.HashedStack;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundContainerClickPacket extends Packet {
    public static final int CLICK_OUTSIDE_NOT_HOLDING_SLOT = -999;

    private int containerId;
    private int stateId;
    private int slot;
    private @NonNull ContainerActionType action;
    private @NonNull ContainerAction param;
    private @Nullable HashedStack carriedItem;
    private @NonNull Int2ObjectMap<@Nullable HashedStack> changedSlots;

    public ServerboundContainerClickPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundContainerClickPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.containerId = MinecraftTypes.readVarInt(in);
        this.stateId = MinecraftTypes.readVarInt(in);
        this.slot = in.readShort();
        byte param = in.readByte();
        this.action = ContainerActionType.from(in.readByte());
        if (this.action == ContainerActionType.CLICK_ITEM) {
            this.param = ClickItemAction.from(param);
        } else if (this.action == ContainerActionType.SHIFT_CLICK_ITEM) {
            this.param = ShiftClickItemAction.from(param);
        } else if (this.action == ContainerActionType.MOVE_TO_HOTBAR_SLOT) {
            this.param = MoveToHotbarAction.from(param);
        } else if (this.action == ContainerActionType.CREATIVE_GRAB_MAX_STACK) {
            if (param == 0) param = 2; // tf
            this.param = CreativeGrabAction.from(param);
        } else if (this.action == ContainerActionType.DROP_ITEM) {
            this.param = DropItemAction.from(param + (this.slot != -999 ? 2 : 0));
        } else if (this.action == ContainerActionType.SPREAD_ITEM) {
            this.param = SpreadItemAction.from(param);
        } else if (this.action == ContainerActionType.FILL_STACK) {
            this.param = FillStackAction.from(param);
        } else {
            throw new IllegalStateException();
        }

        int changedItemsSize = MinecraftTypes.readVarInt(in);
        this.changedSlots = new Int2ObjectOpenHashMap<>(changedItemsSize);
        for (int i = 0; i < changedItemsSize; i++) {
            int key = in.readShort();
            HashedStack value = MinecraftTypes.readNullable(in, MinecraftTypes::readHashedStack);
            this.changedSlots.put(key, value);
        }

        this.carriedItem = MinecraftTypes.readNullable(in, MinecraftTypes::readHashedStack);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.containerId);
        MinecraftTypes.writeVarInt(out, this.stateId);
        out.writeShort(this.slot);

        int param = this.param.getId();
        if (this.action == ContainerActionType.DROP_ITEM) {
            param %= 2;
        }

        out.writeByte(param);
        out.writeByte(this.action.ordinal());

        MinecraftTypes.writeVarInt(out, this.changedSlots.size());
        for (Int2ObjectMap.Entry<HashedStack> pair : this.changedSlots.int2ObjectEntrySet()) {
            out.writeShort(pair.getIntKey());
            MinecraftTypes.writeNullable(out, pair.getValue(), MinecraftTypes::writeHashedStack);
        }

        MinecraftTypes.writeNullable(out, this.carriedItem, MinecraftTypes::writeHashedStack);
    }
}
