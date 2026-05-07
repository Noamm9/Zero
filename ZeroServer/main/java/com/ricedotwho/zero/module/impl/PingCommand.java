package com.ricedotwho.zero.module.impl;

import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
import com.ricedotwho.mcprotocol.protocol.net.registry.PacketDirection;
import com.ricedotwho.mcprotocol.protocol.packet.ping.clientbound.ClientboundPongResponsePacket;
import com.ricedotwho.mcprotocol.protocol.packet.ping.severbound.ServerboundPingRequestPacket;
import com.ricedotwho.zero.event.packet.PacketContext;
import com.ricedotwho.zero.event.packet.PacketEvent;
import com.ricedotwho.zero.module.Module;
import com.ricedotwho.zero.util.ChatUtil;
import com.ricedotwho.zero.util.command.CommandBase;

public class PingCommand extends Module {
    private long sending = 0;

    public PingCommand(MinecraftClient proxy) {
        super("PingCommand", proxy);
        this.enabled = true;
        this.canDisable = false;

        this.getProxy().getModule(Command.class).register("ping", new CommandBase("Gets the proxy ping", "", args -> {
            sending = System.currentTimeMillis();
            this.sendServer(new ServerboundPingRequestPacket(sending));
        }));
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onCBPing(PacketContext<ClientboundPongResponsePacket> ctx) {
        if (sending != 0) {
            ctx.getPacket().lazyDecode();
            if (sending == ctx.getPacket().getPingTime()) {
                ctx.setCancelled(true);
                ChatUtil.prefix(this.getProxy(), "Ping: " + (System.currentTimeMillis() - sending));
                sending = 0;
            }
        }
    }
}
