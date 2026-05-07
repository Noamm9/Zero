package com.ricedotwho.zero.module.impl;

import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
import com.ricedotwho.mcprotocol.protocol.net.registry.PacketDirection;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.ClientboundRespawnPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level.ClientboundBlockUpdatePacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level.ClientboundSectionBlocksUpdatePacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.player.*;
import com.ricedotwho.mcprotocol.protocol.packet.login.clientbound.ClientboundLoginFinishedPacket;
import com.ricedotwho.zero.event.custom.EventHandler;
import com.ricedotwho.zero.event.custom.events.ServerTickEvent;
import com.ricedotwho.zero.event.packet.PacketContext;
import com.ricedotwho.zero.event.packet.PacketEvent;
import com.ricedotwho.zero.module.Module;
import com.ricedotwho.zero.module.impl.sequence.SequenceManager;
import com.ricedotwho.zero.module.setting.BooleanSetting;
import com.ricedotwho.zero.module.setting.NumberSetting;
import com.ricedotwho.zero.module.setting.TextSetting;
import com.ricedotwho.zero.util.*;
import com.ricedotwho.zero.util.rotation.Rot;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.cloudburstmc.math.vector.Vector3i;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.PlayerAction;
import org.geysermc.mcprotocollib.protocol.data.game.level.block.BlockChangeEntry;

import java.util.*;
import java.util.List;

public class StateSS extends Module {

    private final BooleanSetting singleSkip = new BooleanSetting("Single Skip", false);
    private final NumberSetting lampTicks = new NumberSetting("Lamp Ticks", 7, 0, 10, 1);
    private final BooleanSetting logging = new BooleanSetting("Logging", false);
    private final NumberSetting delay = new NumberSetting("delay", 50, 0, 200, 5);
    private final BooleanSetting delayFirst = new BooleanSetting("Delay First", false);
    private final BooleanSetting doAutoStart = new BooleanSetting("Do autostart", false);
    private final TextSetting autoStart = new TextSetting("Autostart", "0,150,300", false, false);

    public StateSS(MinecraftClient proxy) {
        super("StateSS", proxy);

        register(singleSkip, lampTicks, delay, delayFirst, doAutoStart, autoStart, logging);

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
    private boolean firstButton = true;
    private Pos clickedButton = null;
    private boolean allExist = false;
    private boolean waitForLamps = false;

    private final Pos startButton = new Pos(110, 121, 91);
    private static final Pos max = new Pos(111, 123, 95);
    private static final Pos min = new Pos(110, 120, 92);
    private final Map<Integer, Boolean> buttons = new HashMap<>();
    private final Map<Integer, Boolean> blocks = new HashMap<>();
    private final List<Button> solution = new ArrayList<>();

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
        firstButton = true;
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
        firstButton = true;
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
        List<Integer> delays = new ArrayList<>();
        for (String s : autoStart.getValue().split(",")) {
            if (Utils.isInteger(s)) {
                delays.add(Integer.parseInt(s));
            } else {
                ChatUtil.prefix(this.getProxy(), Component.text("Bad autostart config!").color(NamedTextColor.RED));
                return;
            }
        }
        delays.forEach(d -> TaskQueue.addTask("StateSSAutoStart:" + d, () -> clickButton(startButton), d));
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
                //startClicks++;
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
                    if (before == button && button) {
                        if (clickedButton != null && allExist && clickedButton.equals(pos.floor()) && !firstButton) {
                            log("clicking next");
                            clickNextButton();
                        }
                        continue;
                    }
                    if (buttons.values().stream().allMatch(v -> v)) {
                        // all spawned
                        onButtonsSpawned();
                        allExist = true;
                    } else if (!solution.isEmpty()) {
                        // buttons despawned
                        TaskQueue.addTask("StateSSCheckLamps", () -> {
                            if (this.blocks.containsValue(true) || buttons.containsValue(true)) return;
                            log("lamp reset");
                            resetSolution();
                            startClicks = 0;
                        }, 20);
                    } else {
                        allExist = false;
                    }
                    continue;
                }

                int index = getButtonIndex(pos);
                if (record.getBlock() == Blocks.SEA_LANTERN) {
                    add(new Button(pos));
                    awaitingButton = true;
                    blocks.put(index, false);
                } else {
                    blocks.put(index, false);
                    Button button = getButtonByPos(pos);
                    if (button != null) {
                        button.setShouldTick(false);
                        if (button.getTicks() < this.lampTicks.getValue()) this.solution.remove(button);
                    }

                    if (blocks.values().stream().noneMatch(v -> v)) {
                        // all spawned
                        TaskQueue.addTask("StateSSCheckLampsForClick", () -> {
                            if (blocks.containsValue(true)) return;
                            onAllLampsGone();
                        }, 20);
                    }
                }
            }
        }
    }

    private Button getButtonByPos(Pos pos) {
        for (Button button : solution) {
            if (button.getPos().equals(pos) || button.getButton().equals(pos)) return button;
        }
        return null;
    }

    private boolean add(Button button) {
        Button button1 = getButtonByPos(button.getPos());
        //log("Button added: " + button.getPos());
        if (button1 != null) {
            int b = solution.indexOf(button1);
            solution.remove(button1);
            button.setTicks(button1.getTicks());
            button.setShouldTick(false);
            solution.add(button);
            log("button existed before b " + b + " a " + solution.indexOf(button));
            return true;
        }
        solution.add(button);
        return false;
    }

    private void clickNextButton() {
        if (solution.isEmpty()) return;
        firstButton = false;
        Pos pos = solution.get(0).getButton();
        clickedButton = pos;
        int index = getButtonIndex(pos);
        if (!buttons.get(index)) {
            awaitingButton = true;
            return;
        }
        solution.remove(0);

        long d = delay.getValue().longValue() - (System.currentTimeMillis() - lastClick);
        TaskQueue.addTask("StateSSClick", () -> clickButton(pos), d);
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

        if (this.singleSkip.getValue() && solution.size() == 3 && /*startClicks == 3 &&*/ first) solution.remove(0);
        long clickDelay = 0;
        if (first && this.delayFirst.getValue()) clickDelay = delay.getValue().longValue();
        first = false;
        if (!awaitingButton) return;
        Pos pos = solution.get(0).getButton();
        int index = getButtonIndex(pos);
        if (!buttons.get(index)) return;
        awaitingButton = false;
        solution.remove(0);
        clickedButton = pos.floor();

        if (clickDelay == 0) {
            clickButton(pos);
//            if (firstButton) {
//                TaskQueue.addTask("StateSSFirstButton", () -> {
//                    if (firstButton) clickButton(startButton);
//                    firstButton = false;
//                }, delay.getValue().longValue() + 50);
//            }
        } else {
            TaskQueue.addTask("StateSSFirstDelay", () -> {
                clickButton(pos);
//                if (firstButton) {
//                    TaskQueue.addTask("StateSSFirstButton", () -> {
//                        if (firstButton) clickButton(startButton);
//                        firstButton = false;
//                    }, delay.getValue().longValue() + 50);
//                }
            }, clickDelay);
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
        if (!this.getProxy().getArea().is(Island.Dungeon) || !atSS() || !inRange(block, pos)) return;
        log("clicking: " + block.floor().toChatString());
        Vector3i vec3i = block.asVec3i();
        SequenceManager.register(SequenceManager.State.ATTACK, () -> {
            this.getProxy().getModule(SequenceManager.class).sendServerSequenced(sequence -> new ServerboundPlayerActionPacket(PlayerAction.START_DIGGING, vec3i, org.geysermc.mcprotocollib.protocol.data.game.entity.object.Direction.NORTH, sequence));
            this.sendServer(new ServerboundSwingPacket(Hand.MAIN_HAND));
            this.sendServer(new ServerboundPlayerActionPacket(PlayerAction.CANCEL_DIGGING, vec3i, org.geysermc.mcprotocollib.protocol.data.game.entity.object.Direction.DOWN, 0));
        });
        lastClick = System.currentTimeMillis();
    }

    @EventHandler
    public void onServerTick(ServerTickEvent event) {
        new ArrayList<>(solution).forEach(b -> {
            if (b.isShouldTick()) b.tick();
        });
    }

    private void log(String message) {
        if (!this.logging.getValue()) return;
        ChatUtil.prefix(this.getProxy(), message + " (" + System.currentTimeMillis() + ")");
    }

    @Getter
    @Setter
    public static class Button {
        private Pos pos;
        private Pos button;
        private boolean shouldTick = true;

        private int ticks = 0;
        public Button(Pos pos) {
            this.pos = pos;
            this.button = pos.subtract(1, 0, 0);
        }

        public void tick() {
            this.ticks++;
        }

        @Override
        public String toString() {
            return "Button{" +
                    "pos=" + pos +
                    ",shouldTick=" + shouldTick +
                    ",ticks=" + ticks +
                    "}";
        }
    }

    private enum Status {
        NONE,
        P2,
        P3
    }
}
