package com.ricedotwho.zero.module.impl;

import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
import com.ricedotwho.mcprotocol.protocol.net.registry.PacketDirection;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.ClientboundRespawnPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity.player.ClientboundEntityEventPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.inventory.ClientboundContainerSetSlotPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.inventory.ServerboundContainerClickPacket;
import com.ricedotwho.mcprotocol.protocol.packet.login.clientbound.ClientboundLoginFinishedPacket;
import com.ricedotwho.zero.event.packet.PacketContext;
import com.ricedotwho.zero.event.packet.PacketEvent;
import com.ricedotwho.zero.module.Module;
import com.ricedotwho.zero.module.setting.NumberSetting;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.geysermc.mcprotocollib.protocol.data.game.entity.EntityEvent;
import org.geysermc.mcprotocollib.protocol.data.game.inventory.ContainerActionType;
import org.geysermc.mcprotocollib.protocol.data.game.inventory.ShiftClickItemAction;
import org.geysermc.mcprotocollib.protocol.data.game.item.HashedStack;
import org.geysermc.mcprotocollib.protocol.data.game.item.ItemStack;

public class AutoTotem extends Module {
    private final NumberSetting delay = new NumberSetting("Delay", 0, 0, 500, 10);

    private int totemSlot = -1;
    private int offhandSlot = -1;
    private int stateId = 0;
    private long lastSwap = 0;
    private boolean needsTotem = false;

    public AutoTotem(MinecraftClient proxy) {
        super("AutoTotem", proxy);
        register(delay);
    }

    private void reset() {
        totemSlot = -1;
        offhandSlot = -1;
        stateId = 0;
        lastSwap = 0;
        needsTotem = false;
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onEntityEvent(PacketContext<ClientboundEntityEventPacket> ctx) {
        if (!this.isEnabled()) return;
        ClientboundEntityEventPacket packet = ctx.getPacket();
        packet.lazyDecode();

        if (packet.getEvent() == EntityEvent.PLAYER_OP_PERMISSION_LEVEL_0) {
            needsTotem = true;
            swapTotem();
        }
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onSlotUpdate(PacketContext<ClientboundContainerSetSlotPacket> ctx) {
        if (!this.isEnabled()) return;
        ClientboundContainerSetSlotPacket packet = ctx.getPacket();
        packet.lazyDecode();

        if (packet.getContainerId() == 0) {
            stateId = packet.getStateId();
            ItemStack item = packet.getItem();

            if (packet.getSlot() == 45) {
                offhandSlot = packet.getSlot();
                if (item == null || item.getId() != 1173) {
                    needsTotem = true;
                    swapTotem();
                } else {
                    needsTotem = false;
                }
            } else if (packet.getSlot() >= 9 && packet.getSlot() <= 44) {
                if (item != null && item.getId() == 1173) {
                    totemSlot = packet.getSlot();
                }
            }
        }
    }

    private void swapTotem() {
        long now = System.currentTimeMillis();
        if (now - lastSwap < delay.getValue().longValue()) return;
        if (!needsTotem) return;
        if (totemSlot == -1) return;

        Int2ObjectMap<HashedStack> changedSlots = new Int2ObjectOpenHashMap<>();
        changedSlots.put(totemSlot, null);
        changedSlots.put(45, null);

        sendServer(new ServerboundContainerClickPacket(
            0,
            stateId,
            totemSlot,
            ContainerActionType.SHIFT_CLICK_ITEM,
            ShiftClickItemAction.LEFT_CLICK,
            null,
            changedSlots
        ));

        lastSwap = now;
        totemSlot = -1;
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onRespawn(PacketContext<ClientboundRespawnPacket> ctx) {
        reset();
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onLogin(PacketContext<ClientboundLoginFinishedPacket> ctx) {
        reset();
    }
}
