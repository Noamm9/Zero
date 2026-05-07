package com.ricedotwho.zero.module.impl;

import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
import com.ricedotwho.mcprotocol.protocol.net.registry.PacketDirection;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.ClientboundRespawnPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level.ClientboundBlockUpdatePacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level.ClientboundSectionBlocksUpdatePacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.player.ServerboundMovePlayerPosPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.player.ServerboundMovePlayerPosRotPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.player.ServerboundUseItemPacket;
import com.ricedotwho.mcprotocol.protocol.packet.login.clientbound.ClientboundLoginFinishedPacket;
import com.ricedotwho.zero.event.packet.PacketContext;
import com.ricedotwho.zero.event.packet.PacketEvent;
import com.ricedotwho.zero.module.Module;
import com.ricedotwho.zero.module.impl.sequence.SequenceManager;
import com.ricedotwho.zero.module.setting.BooleanSetting;
import com.ricedotwho.zero.module.setting.NumberSetting;
import com.ricedotwho.zero.util.*;
import com.ricedotwho.zero.util.rotation.Rot;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;
import org.geysermc.mcprotocollib.protocol.data.game.level.block.BlockChangeEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Auto4 extends Module {
    private final BooleanSetting term = new BooleanSetting("Terminator", false);
    private final NumberSetting delay = new NumberSetting("Delay", 200, 0, 1000,10);

    private final Pos pos = new Pos(0, 0, 0);
    private Section section = Section.NONE;
    private long lastShot = 0;

    private static final List<Pos> blocks = Arrays.asList(
            new Pos(68, 130, 50), new Pos(66, 130, 50), new Pos(64, 130, 50),
            new Pos(68, 128, 50), new Pos(66, 128, 50), new Pos(64, 128, 50),
            new Pos(68, 126, 50), new Pos(66, 126, 50), new Pos(64, 126, 50)

    );

    private final List<Integer> done = new ArrayList<>();

    public Auto4(MinecraftClient proxy) {
        super("Auto4", proxy);
        register(
                term,
                delay
        );
    }

    private void reset() {
        pos.set(0, 0, 0);
        section = Section.NONE;
        done.clear();
        lastShot = System.currentTimeMillis();
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onBlockChange(PacketContext<ClientboundBlockUpdatePacket> ctx) {
        if (this.getProxy().getArea().is(Island.Dungeon) && section == Section.P3 && on4thDev()) {
            ClientboundBlockUpdatePacket packet = ctx.getPacket();
            packet.lazyDecode();
            onBlockChange(packet.getEntry());
        }
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onMultiBlockChange(PacketContext<ClientboundSectionBlocksUpdatePacket> ctx) {
        if (this.getProxy().getArea().is(Island.Dungeon) && section == Section.P3 && on4thDev()) {
            ClientboundSectionBlocksUpdatePacket packet = ctx.getPacket();
            packet.lazyDecode();
            onBlockChange(packet.getEntries());
        }
    }

    public void onBlockChange(BlockChangeEntry...records) {
        for (BlockChangeEntry record : records) {
            Pos position = new Pos(record.getPosition());
            int index = blocks.indexOf(position);
            if (index == -1) continue;

            if (record.getBlock() == Blocks.BLUE_STAINED_TERRACOTTA) {
                done.add(index);
            }

            if (record.getBlock() != Blocks.EMERALD_BLOCK) return;

            long now = System.currentTimeMillis();
            long delay = this.delay.getValue().longValue() - (now - lastShot);

            Rot rot = calculateAim(position, index);

            TaskQueue.addTask("Auto4Shoot" + now, () -> {
                shoot(rot.yaw, rot.pitch);
                lastShot = System.currentTimeMillis();
            }, delay);
        }
    }

    private void shoot(float yaw, float pitch) {
        SequenceManager.register(SequenceManager.State.USE, () -> this.getProxy().getModule(SequenceManager.class).sendServerSequenced(sequence -> new ServerboundUseItemPacket(Hand.MAIN_HAND, sequence, yaw, pitch)));
    }

    private boolean on4thDev() {
        return pos.x() > 63 && pos.x() < 64
                && pos.y() == 127
                && pos.z() > 35 && pos.z() < 36;
    }

    private boolean ignorePos() {
        return !this.getProxy().getArea().is(Island.Dungeon) || section == Section.NONE;
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onRespawn(PacketContext<ClientboundRespawnPacket> ctx) {
        reset();
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onLogin(PacketContext<ClientboundLoginFinishedPacket> ctx) {
        reset();
    }

    @PacketEvent(direction = PacketDirection.SERVERBOUND)
    public void onPosition(PacketContext<ServerboundMovePlayerPosPacket> ctx) {
        if (ignorePos()) return;
        ServerboundMovePlayerPosPacket packet = ctx.getPacket();
        packet.lazyDecode();
        pos.set(packet.getX(), packet.getY(), packet.getZ());
    }

    @PacketEvent(direction = PacketDirection.SERVERBOUND)
    public void onPositionRotation(PacketContext<ServerboundMovePlayerPosRotPacket> ctx) {
        if (ignorePos()) return;
        ServerboundMovePlayerPosRotPacket packet = ctx.getPacket();
        packet.lazyDecode();
        pos.set(packet.getX(), packet.getY(), packet.getZ());
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onChat(PacketContext<ClientboundSystemChatPacket> ctx) {
        if (!this.getProxy().getArea().is(Island.Dungeon)) return;
        ClientboundSystemChatPacket packet = ctx.getPacket();
        packet.lazyDecode();

        String message = ChatUtil.stripFormatting(ChatUtil.getContent(packet.getContent()));

        switch (message) {
            case "[BOSS] Goldor: Who dares trespass into my domain?", "[DS] Starting S1 on next tick":
                section = Section.P3;
                break;
            case "[BOSS] Goldor: You have done it, you destroyed the factory…":
                section = Section.NONE;
                break;
            case "[BOSS] Storm: Pathetic Maxor, just like expected.":
                section = Section.P2;
                break;
        }
    }

    private Rot calculateAim(Pos pos, int index) {
        Pos target = new Pos(pos);

        if (!this.term.getValue()) return this.getYawPitch(target.add(0.5, 1, 0));

        switch (index % 3) {
            case 0:
                target.selfAdd(
                        -0.5,
                        1,
                        0
                );
                break;
            case 1:
                boolean f1 = done.contains(index - 1), f2 = this.done.contains(index + 1);
                if (f1 && !f2) {
                    target.selfAdd(-0.5, 1, 0);
                } else if (f2 && !f1) {
                    target.selfAdd(1.5, 1, 0);
                } else {
                    // ??????
                    target.selfAdd(0.5 + (Math.random() < 0.5 ? -1 : 1), 1, 0);
                }
                break;
            case 2:
                target.selfAdd(
                        1.5,
                        1,
                        0
                );
                break;
            default:
                target.selfAdd(
                        0.5,
                        1,
                        0
                );
        }
        return this.getYawPitch(target);
    }

    private Rot getYawPitch(Pos position) {
        Pos dPos = position.subtract(this.pos.add(0, 1.62, 0));

        double distance = Math.sqrt(dPos.x() * dPos.x() + dPos.z() * dPos.z());
        float yaw = (float) (Math.atan2(-dPos.x(), dPos.z()) * 180 / Math.PI);
        float pitch = (float) (-Math.atan2(dPos.y(), distance) * 180 / Math.PI);

        return new Rot(pitch, yaw);
    }

    private enum Section {
        NONE,
        P2,
        P3
    }
}
