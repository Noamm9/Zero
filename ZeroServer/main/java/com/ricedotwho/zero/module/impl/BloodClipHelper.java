package com.ricedotwho.zero.module.impl;

import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
import com.ricedotwho.mcprotocol.protocol.net.registry.PacketDirection;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.ClientboundRespawnPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerPositionPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.player.ServerboundUseItemPacket;
import com.ricedotwho.mcprotocol.protocol.packet.login.clientbound.ClientboundLoginFinishedPacket;
import com.ricedotwho.mcprotocol.utils.ByteBufUtils;
import com.ricedotwho.zero.event.custom.EventHandler;
import com.ricedotwho.zero.event.custom.events.ZeroPayloadEvent;
import com.ricedotwho.zero.event.packet.PacketContext;
import com.ricedotwho.zero.event.packet.PacketEvent;
import com.ricedotwho.zero.module.Module;
import com.ricedotwho.zero.module.impl.sequence.SequenceManager;
import com.ricedotwho.zero.util.ChatUtil;
import com.ricedotwho.zero.util.Island;
import net.kyori.adventure.key.Key;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;

import java.io.IOException;

public class BloodClipHelper extends Module {
    private int roofHeight = 0;
    private boolean shouldPearl = false;

    private static final Key START_KEY = Key.key("zero", "bloodcliphelper/start");
    private static final Key STOP_KEY = Key.key("zero", "bloodcliphelper/stop");

    public BloodClipHelper(MinecraftClient proxy) {
        super("BCH", proxy);
    }

    private void reset() {
        roofHeight = 0;
        shouldPearl = false;
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onPosition(PacketContext<ClientboundPlayerPositionPacket> ctx) {
        if (!shouldPearl || roofHeight == 0 || !this.getProxy().getArea().is(Island.Dungeon)) return;
        ClientboundPlayerPositionPacket packet = ctx.getPacket();
        packet.lazyDecode();
        double y = packet.getPosition().getY();
        if (y > 82 && y < roofHeight) {
            throwPearl();
        } else {
            reset();
            ChatUtil.prefix(this.getProxy(), "Done pearling");
        }
    }

    private void throwPearl() {
        SequenceManager.register(SequenceManager.State.USE, () -> this.getProxy().getModule(SequenceManager.class).sendServerSequenced(sequence -> new ServerboundUseItemPacket(Hand.MAIN_HAND, sequence, 0, -90)));
    }

    @EventHandler
    public void onPayload(ZeroPayloadEvent event) {
        if (event.getKey().equals(START_KEY)) {
            try {
                roofHeight = ByteBufUtils.readVarInt(event.getData());
                shouldPearl = true;
                ChatUtil.prefix(this.getProxy(), "Roof height: " + roofHeight);

                // throw the first pearl yeah
                throwPearl();
            } catch (IOException e) {
                roofHeight = -1;
                ChatUtil.prefix(this.getProxy(), "Failed to parse roof height!");
            }
        } else if (event.getKey().equals(STOP_KEY)) {
            ChatUtil.prefix(this.getProxy(), "Stopping BCH");
            reset();
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
