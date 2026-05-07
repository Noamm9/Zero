package com.ricedotwho.zero.module.impl;

import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
import com.ricedotwho.mcprotocol.protocol.net.registry.PacketDirection;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.ClientboundRespawnPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level.ClientboundBlockUpdatePacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level.ClientboundSectionBlocksUpdatePacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.player.*;
import com.ricedotwho.mcprotocol.protocol.packet.login.clientbound.ClientboundLoginFinishedPacket;
import com.ricedotwho.zero.event.packet.PacketContext;
import com.ricedotwho.zero.event.packet.PacketEvent;
import com.ricedotwho.zero.module.Module;
import com.ricedotwho.zero.module.impl.sequence.SequenceManager;
import com.ricedotwho.zero.module.setting.BooleanSetting;
import com.ricedotwho.zero.module.setting.NumberSetting;
import com.ricedotwho.zero.module.setting.TextSetting;
import com.ricedotwho.zero.util.*;
import com.ricedotwho.zero.util.rotation.Rot;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.cloudburstmc.math.vector.Vector3i;
import org.geysermc.mcprotocollib.protocol.data.game.entity.object.Direction;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.PlayerAction;
import org.geysermc.mcprotocollib.protocol.data.game.level.block.BlockChangeEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoSS extends Module {

    private final BooleanSetting singleSkip = new BooleanSetting("Single Skip", false);
    private final BooleanSetting logging = new BooleanSetting("Logging", false);
    private final NumberSetting delay = new NumberSetting("Delay", 150, 0, 200, 5);
    private final BooleanSetting delayFirst = new BooleanSetting("Delay First", false);
    private final BooleanSetting doAutoStart = new BooleanSetting("Do autostart", false);
    private final TextSetting autoStart = new TextSetting("Autostart", "0,120,240", false, false);
    private final BooleanSetting usePacketOrder = new BooleanSetting("Respect packet order", true);

    public AutoSS(MinecraftClient proxy) {
        super("AutoSS", proxy);

        register(singleSkip, delay, delayFirst, doAutoStart, autoStart, usePacketOrder, logging);

        for (int i = 0; i < 16; i++) {
            buttons.put(i, false);
            blocks.put(i, false);
        }
    }

    private final Pos pos = new Pos(0, 0, 0);
    private final Rot rot = new Rot(0, 0);
    private Status status = Status.NONE;
    private boolean awaitingButton = false;
    private boolean first = true;
    private int startClicks = 0;

    private long lastClick = 0;
    private Pos clickedButton = null;
    private boolean allExist = false;
    private boolean waitForLamps = false;

    private final Pos startButton = new Pos(110, 121, 91);
    private static final Pos max = new Pos(111, 123, 95);
    private static final Pos min = new Pos(110, 120, 92);
    private final Map<Integer, Boolean> buttons = new HashMap<>();
    private final Map<Integer, Boolean> blocks = new HashMap<>();
    private final List<Pos> solution = new ArrayList<>();

    private void reset() {
        this.rot.set(0, 0);
        this.pos.set(0, 0, 0);
        this.status = Status.NONE;
        solution.clear();
        for (int i = 0; i < 16; i++) {
            buttons.put(i, false);
            blocks.put(i, false);
        }
        awaitingButton = false;
        first = true;
        startClicks = 0;
        lastClick = 0;
        clickedButton = null;
        allExist = false;
        waitForLamps = false;
    }

    private void resetSolution() {
        log("Resetting solution");
        solution.clear();
        awaitingButton = false;
        clickedButton = null;
        waitForLamps = false;
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onChat(PacketContext<ClientboundSystemChatPacket> ctx) {
        if (!this.getProxy().getArea().is(Island.Dungeon)) return;
        ClientboundSystemChatPacket packet = ctx.getPacket();
        packet.lazyDecode();

        String message = ChatUtil.stripFormatting(ChatUtil.getContent(packet.getContent()));

        switch (message) {
            case "[BOSS] Goldor: Who dares trespass into my domain?":
                status = Status.P3;
                if (this.doAutoStart.getValue() && atSS()) autoStart();
                break;
            case "[BOSS] Storm: Pathetic Maxor, just like expected.", "[DS] Starting S1 on next tick":
                status = Status.P2;
                break;
            case "The Core entrance is opening!":
                status = Status.NONE;
                break;
        }
    }

    private void autoStart() {
        ChatUtil.prefix(this.getProxy(), "Starting SS");
        List<Integer> delays = new ArrayList<>();
        for (String s : autoStart.getValue().split(",")) {
            if (Utils.isInteger(s)) {
                delays.add(Integer.parseInt(s));
            } else {
                ChatUtil.prefix(this.getProxy(), Component.text("Bad autostart config!").color(NamedTextColor.RED));
                return;
            }
        }
        delays.forEach(d -> TaskQueue.addTask("AutoSSAutoStart:" + d, () -> clickButton(startButton), d));
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onRespawn(PacketContext<ClientboundRespawnPacket> ctx) {
        reset();
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onLogin(PacketContext<ClientboundLoginFinishedPacket> ctx) {
        reset();
    }

    private boolean ignorePos() {
        return !this.getProxy().getArea().is(Island.Dungeon) || status.equals(Status.NONE);
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
        rot.setPitch(packet.getPitch());
        rot.setYaw(packet.getYaw());
    }

    @PacketEvent(direction = PacketDirection.SERVERBOUND)
    public void onRotation(PacketContext<ServerboundMovePlayerRotPacket> ctx) {
        if (ignorePos()) return;
        ServerboundMovePlayerRotPacket packet = ctx.getPacket();
        packet.lazyDecode();
        rot.setPitch(packet.getPitch());
        rot.setYaw(packet.getYaw());
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onBlockChange(PacketContext<ClientboundBlockUpdatePacket> ctx) {
        if (this.getProxy().getArea().is(Island.Dungeon)) {
            ClientboundBlockUpdatePacket packet = ctx.getPacket();
            packet.lazyDecode();
            onBlockChange(packet.getEntry());
        }
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onMultiBlockChange(PacketContext<ClientboundSectionBlocksUpdatePacket> ctx) {
        if (this.getProxy().getArea().is(Island.Dungeon)) {
            ClientboundSectionBlocksUpdatePacket packet = ctx.getPacket();
            packet.lazyDecode();
            onBlockChange(packet.getEntries());
        }
    }

    public void onBlockChange(BlockChangeEntry...records) {
        for (BlockChangeEntry record : records) {
            Pos pos = new Pos(record.getPosition());
            if (pos.equals(startButton)) {
                startClicks++;
                log("resetting from start click");
                resetSolution();
                first = true;
                continue;
            }
            if (Utils.isInside(pos, min, max)) {
                if (pos.x() == 110) {
                    int index = getButtonIndex(pos);
                    boolean button = record.getBlock() == Blocks.STONE_BUTTON;
                    Boolean before = buttons.get(index);
                    buttons.put(index, button);

                    // does this block state match the last one?
                    // it should be an update saying like hey u cant break that when u left click the button,
                    // we can use that as the server accepting the click
                    if (before == button && button) {
                        if (clickedButton != null && allExist && clickedButton.equals(pos.floor())) {
                            log("clicking next");
                            clickNextButton();
                        }
                        continue;
                    }

                    if (buttons.values().stream().allMatch(v -> v)) {
                        // all spawned
                        onButtonsSpawned();
                        allExist = true;
                    } /* else if (!solution.isEmpty()) {
                        // buttons despawned
                        log("lamp reset is being run? solution: " + solution);
                        TaskQueue.addTask("StateSSCheckLamps", () -> {
                            if (this.blocks.containsValue(true) || buttons.containsValue(true)) return;
                            log("lamp reset");
                            resetSolution();
                            startClicks = 0;
                        }, 20);
                    } */else {
                        allExist = false;
                    }
                    continue;
                }

                int index = getButtonIndex(pos);
                if (record.getBlock() == Blocks.SEA_LANTERN) {
                    solution.add(pos);
                    awaitingButton = true;
                    blocks.put(index, false);
                } else {
                    blocks.put(index, false);
                    if (blocks.values().stream().noneMatch(v -> v)) {
                        // all spawned
                        TaskQueue.addTask("AutoSSCheckLampsForClick", () -> {
                            if (blocks.containsValue(true)) return;
                            onAllLampsGone();
                        }, 20);
                    }
                }
            }
        }
    }

    private void clickNextButton() {
        if (solution.isEmpty()) return;
        Pos pos = solution.get(0).add(-1, 0, 0);
        clickedButton = null;
        int index = getButtonIndex(pos);
        if (!buttons.get(index)) {
            awaitingButton = true;
            return;
        }
        clickedButton = pos;
        solution.remove(0);

        long d = delay.getValue().longValue() - (System.currentTimeMillis() - lastClick);
        TaskQueue.addTask("AutoSSClick", () -> clickButton(pos), d);
    }

    private int getButtonIndex(Pos pos) {
        Pos p = pos.subtract(min);
        // find its index from relative position
        return (p.bZ() % 4) + ((3 - p.bY()) * 4);
    }

    private void onAllLampsGone() {
        if (!waitForLamps) return;
        onButtonsSpawned();
    }

    private void onButtonsSpawned() {
        //log("all buttons spawned. Sol: " + solution.size());
        if (solution.isEmpty()) return;

        if (blocks.containsValue(true)) {
            waitForLamps = true;
            return;
        }

        if (this.singleSkip.getValue() && solution.size() == 3 && startClicks == 3 && first) solution.remove(0);
        long clickDelay = 0;
        if (first && this.delayFirst.getValue()) clickDelay = delay.getValue().longValue();
        first = false;
        startClicks = 0;
        if (!awaitingButton) return;
        Pos pos = solution.get(0);
        int index = getButtonIndex(pos);
        if (!buttons.get(index)) return;
        awaitingButton = false;
        solution.remove(0);
        clickedButton = pos.floor().add(-1, 0, 0);

        if (clickDelay == 0) {
            clickButton(clickedButton);
        } else {
            TaskQueue.addTask("AutoSSFirstDelay", () -> clickButton(clickedButton), clickDelay);
        }
    }

    private boolean atSS() {
        Pos p = pos.add(0, 1.62, 0);
        // distance to dev centers
        return Utils.calcDist(p.x(), p.y(), p.z(), 111, 120, 94) < 25.0;
    }

    private boolean inRange(Pos a, Pos b) {
        return Utils.calcDist(a, b) < 25.0;
    }

    private void clickButton(Pos block) {
        if (!this.getProxy().getArea().is(Island.Dungeon) || !atSS()) return;
        if (!inRange(block, pos)) {
            ChatUtil.prefix(this.getProxy(), "Out of range for " + block.toChatString() + "!");
            return;
        }
        Vector3i vec3i = block.asVec3i();

        if (usePacketOrder.getValue()) {
            SequenceManager.register(SequenceManager.State.ATTACK, () -> {
                log("clicking: " + block.floor().toChatString() + " after " + (System.currentTimeMillis() - lastClick) + "ms");
                this.getProxy().getModule(SequenceManager.class).sendServerSequenced(sequence -> new ServerboundPlayerActionPacket(PlayerAction.START_DIGGING, vec3i, Direction.WEST, sequence));
                this.sendServer(new ServerboundSwingPacket(Hand.MAIN_HAND));
                this.sendServer(new ServerboundPlayerActionPacket(PlayerAction.CANCEL_DIGGING, vec3i, Direction.DOWN, 0));
                lastClick = System.currentTimeMillis();
            });
        } else {
            log("clicking: " + block.floor().toChatString() + " after " + (System.currentTimeMillis() - lastClick) + "ms");
            this.getProxy().getModule(SequenceManager.class).sendServerSequenced(sequence -> new ServerboundPlayerActionPacket(PlayerAction.START_DIGGING, vec3i, Direction.WEST, sequence));
            this.sendServer(new ServerboundSwingPacket(Hand.MAIN_HAND));
            this.sendServer(new ServerboundPlayerActionPacket(PlayerAction.CANCEL_DIGGING, vec3i, Direction.DOWN, 0));
            lastClick = System.currentTimeMillis();
        }
    }

    private void log(String message) {
        if (!this.logging.getValue()) return;
        ChatUtil.prefix(this.getProxy(), message + " (" + System.currentTimeMillis() + ")");
    }

    private enum Status {
        NONE,
        P2,
        P3
    }
}