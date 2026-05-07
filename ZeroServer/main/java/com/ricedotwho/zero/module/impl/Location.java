package com.ricedotwho.zero.module.impl;

import com.ricedotwho.mcprotocol.data.PlayerListEntry;
import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
import com.ricedotwho.mcprotocol.protocol.net.registry.PacketDirection;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.ClientboundPlayerInfoUpdatePacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.ClientboundRespawnPacket;
import com.ricedotwho.zero.event.packet.PacketContext;
import com.ricedotwho.zero.event.packet.PacketEvent;
import com.ricedotwho.zero.module.Module;
import com.ricedotwho.zero.util.ChatUtil;
import com.ricedotwho.zero.util.Island;
import com.ricedotwho.zero.util.command.CommandBase;

public class Location extends Module {

    public Location(MinecraftClient proxy) {
        super("Location", proxy);
        this.enabled = true;
        this.canDisable = false;

        this.getProxy().getModule(Command.class).register("loc", new CommandBase("Sends the player Location", "", args -> ChatUtil.prefix(this.getProxy(), "Location: " + this.getProxy().getArea())));
    }

    private void reset() {
        this.getProxy().setArea(Island.Unknown);
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND, async = true)
    public void onPlayerList(PacketContext<ClientboundPlayerInfoUpdatePacket> ctx) {
        ClientboundPlayerInfoUpdatePacket packet = ctx.getPacket();
        packet.lazyDecode();

        for (PlayerListEntry playerEntry : packet.getEntries()) {
            String display = ChatUtil.getContent(playerEntry.getDisplayName());
            if (!display.isBlank()) {
                if (display.startsWith("Area: ") || display.startsWith("Dungeon: ")) {
                    Island newArea = Island.findByName(display);
                    Island oldArea = this.getProxy().getArea();
                    if(!newArea.equals(oldArea)) {
                        this.getProxy().setArea(newArea);
                    }
                }
            }
        }
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onRespawn(PacketContext<ClientboundRespawnPacket> ctx) {
        reset();
    }
}
