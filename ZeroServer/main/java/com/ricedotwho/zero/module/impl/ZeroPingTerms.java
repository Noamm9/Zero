package com.ricedotwho.zero.module.impl;

import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
import com.ricedotwho.mcprotocol.protocol.net.registry.PacketDirection;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.ClientboundRespawnPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.inventory.*;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.inventory.*;
import com.ricedotwho.mcprotocol.protocol.packet.login.clientbound.ClientboundLoginFinishedPacket;
import com.ricedotwho.zero.event.packet.PacketContext;
import com.ricedotwho.zero.event.packet.PacketEvent;
import com.ricedotwho.zero.module.Module;
import com.ricedotwho.zero.module.setting.BooleanSetting;
import com.ricedotwho.zero.util.ChatUtil;
import com.ricedotwho.zero.util.Container;
import com.ricedotwho.zero.util.Island;
import com.ricedotwho.zero.util.Utils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import org.geysermc.mcprotocollib.protocol.data.game.inventory.ContainerAction;
import org.geysermc.mcprotocollib.protocol.data.game.inventory.ContainerActionType;
import org.geysermc.mcprotocollib.protocol.data.game.inventory.ContainerType;
import org.geysermc.mcprotocollib.protocol.data.game.item.HashedStack;
import org.geysermc.mcprotocollib.protocol.data.game.item.ItemStack;

import java.util.regex.Pattern;

public class ZeroPingTerms extends Module {
    private final BooleanSetting forceDungeon = new BooleanSetting("Force Dungeon", false);
    private final Pattern termPattern = Pattern.compile("^Select all the [\\w ]+ items!$|^Click in order!$|^Correct all the panes!$|^Change all to same color!$|^What starts with: '\\w'\\?$");
    private boolean inTerminal = false;

    private Container container;

    public ZeroPingTerms(MinecraftClient proxy) {
        super("ZeroPingTerms", proxy);
        register(forceDungeon);
    }

    private void reset() {
        inTerminal = false;
        container = null;
    }

    private boolean check() {
        return (this.getProxy().getArea().is(Island.Dungeon) || forceDungeon.getValue()) && container != null;
    }

    // SERVER -> CLIENT

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onServerOpenWindow(PacketContext<ClientboundOpenScreenPacket> ctx) {
        if (!this.getProxy().getArea().is(Island.Dungeon)) return;
        ClientboundOpenScreenPacket packet = ctx.getPacket();
        packet.lazyDecode();

        if (!Utils.equalsOneOf(packet.getType(), ContainerType.GENERIC_9X4, ContainerType.GENERIC_9X5, ContainerType.GENERIC_9X6)) return;

        String name;
        if (packet.getTitle() instanceof TranslatableComponent text) {
            name = text.key();
        } else if (packet.getTitle() instanceof TextComponent text) {
            if (text.children().size() == 1) {
                name = ((TextComponent) text.children().get(0)).content();
            } else {
                name = text.content();
            }
        } else {
            return;
        }

        container = Container.create(packet.getContainerId(), packet.getType(), name);
        if (container == null) return;

        this.inTerminal = termPattern.matcher(name).find();
        if (this.inTerminal) {
            packet.setContainerId(127);
            packet.setModified(true);
        }
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onServerMountScreenOpen(PacketContext<ClientboundMountScreenOpenPacket> ctx) {
        if (!check()) return;
        this.inTerminal = false;
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onServerCloseWindow(PacketContext<ClientboundContainerClosePacket> ctx) {
        if (!check()) return;
        ctx.getPacket().lazyDecode();
        if (this.inTerminal && ctx.getPacket().getContainerId() == container.windowId) {
            ctx.getPacket().setContainerId(127);
            ctx.getPacket().setModified(true);
        }
        container = null;
        this.inTerminal = false;
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onServerSetSlot(PacketContext<ClientboundContainerSetSlotPacket> ctx) {
        if (!this.inTerminal || !check()) return;
        ClientboundContainerSetSlotPacket packet = ctx.getPacket();
        packet.lazyDecode();
        if (packet.getContainerId() == container.windowId) {
            packet.setContainerId(127);
            packet.setModified(true);
            container.setSlot(packet.getSlot(), packet.getStateId(), packet.getItem());
        }
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onServerSetItems(PacketContext<ClientboundContainerSetDataPacket> ctx) {
        if (!this.inTerminal || !check()) return;
        ctx.getPacket().lazyDecode();
        if (ctx.getPacket().getContainerId() == container.windowId) {
            ctx.getPacket().setContainerId(127);
            ctx.getPacket().setModified(true);
        }
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onServerSetContent(PacketContext<ClientboundContainerSetContentPacket> ctx) {
        if (!this.inTerminal || !check()) return;
        ClientboundContainerSetContentPacket packet = ctx.getPacket();
        packet.lazyDecode();
        if (packet.getContainerId() == container.windowId) {
            packet.setContainerId(127);
            packet.setModified(true);
            for (int i = 0; i < packet.getItems().length; i++) {
                ItemStack item = packet.getItems()[i];
                container.setSlot(i, packet.getStateId(), item);
            }
            container.setCarried(packet.getCarriedItem());
        }
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onServerMerchantOffer(PacketContext<ClientboundMerchantOffersPacket> ctx) {
        if (!this.inTerminal || !check()) return;
        ctx.getPacket().lazyDecode();
        if (ctx.getPacket().getContainerId() == container.windowId) {
            ctx.getPacket().setContainerId(127);
            ctx.getPacket().setModified(true);
        }
    }

    // CLIENT -> SERVER

    @PacketEvent(direction = PacketDirection.SERVERBOUND)
    public void onClientCloseWindow(PacketContext<ServerboundContainerClosePacket> ctx) {
        if (!this.inTerminal || !check()) return;
        ctx.getPacket().lazyDecode();
        if (ctx.getPacket().getContainerId() == 127) {
            ctx.getPacket().setContainerId(container.windowId);
            ctx.getPacket().setModified(true);
        }
        container = null;
        this.inTerminal = false;
    }

    @PacketEvent(direction = PacketDirection.SERVERBOUND)
    public void onClientWindowAction(PacketContext<ServerboundContainerButtonClickPacket> ctx) {
        if (!this.inTerminal || !check()) return;
        ctx.getPacket().lazyDecode();
        if (ctx.getPacket().getContainerId() == 127) {
            ctx.getPacket().setContainerId(container.windowId);
            ctx.getPacket().setModified(true);
        }
    }

    @PacketEvent(direction = PacketDirection.SERVERBOUND)
    public void onClientClick(PacketContext<ServerboundContainerClickPacket> ctx) {
        if (!this.inTerminal || !check()) return;
        ServerboundContainerClickPacket packet = ctx.getPacket();
        packet.lazyDecode();
        if (packet.getContainerId() == 127) {
            packet.setContainerId(container.windowId);
            packet.setModified(true);

            ContainerAction param = packet.getParam();
            ContainerActionType action = packet.getAction();

            if (!Utils.equalsOneOf(action, ContainerActionType.CLICK_ITEM, ContainerActionType.CREATIVE_GRAB_MAX_STACK)) {
                ChatUtil.prefix(this.getProxy(), "Warn: You should only use CLONE or PICKUP click types! you used: " + param + " (" + action + ")");
                return;
            }

            Int2ObjectMap<HashedStack> map = container.simulateClick(packet.getSlot(), param, action);
            packet.setChangedSlots(map);
            packet.setCarriedItem(container.getCarriedHash());
        }
    }

    @PacketEvent(direction = PacketDirection.SERVERBOUND)
    public void onClientSlotChanged(PacketContext<ServerboundContainerSlotStateChangedPacket> ctx) {
        if (!this.inTerminal || !this.getProxy().getArea().is(Island.Dungeon)) return;
        ctx.getPacket().lazyDecode();
        if (ctx.getPacket().getContainerId() == 127) {
            ctx.getPacket().setContainerId(container.windowId);
            ctx.getPacket().setModified(true);
        }
    }

    @PacketEvent(direction = PacketDirection.SERVERBOUND)
    public void onClientPlaceRecipe(PacketContext<ServerboundPlaceRecipePacket> ctx) {
        if (!this.inTerminal || !this.getProxy().getArea().is(Island.Dungeon)) return;
        ctx.getPacket().lazyDecode();
        if (ctx.getPacket().getContainerId() == 127) {
            ctx.getPacket().setContainerId(container.windowId);
            ctx.getPacket().setModified(true);
        }
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
