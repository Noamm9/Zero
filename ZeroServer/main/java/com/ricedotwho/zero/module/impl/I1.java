//package com.ricedotwho.zero.module.impl;
//
//import com.ricedotwho.mcprotocol.data.game.EntityMetadata;
//import com.ricedotwho.mcprotocol.data.game.Position;
//import com.ricedotwho.mcprotocol.data.game.values.entity.MetadataType;
//import com.ricedotwho.mcprotocol.data.game.values.player.Face;
//import com.ricedotwho.mcprotocol.data.game.values.player.PlayerAction;
//import com.ricedotwho.mcprotocol.data.game.values.world.block.BlockChangeRecord;
//import com.ricedotwho.mcprotocol.data.game.values.world.sound.GenericSound;
//import com.ricedotwho.mcprotocol.data.message.Message;
//import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
//import com.ricedotwho.mcprotocol.protocol.net.registry.Direction;
//import com.ricedotwho.mcprotocol.protocol.packet.login.server.LoginSuccessPacket;
//import com.ricedotwho.mcprotocol.protocol.packet.play.client.player.*;
//import com.ricedotwho.mcprotocol.protocol.packet.play.server.ServerChatPacket;
//import com.ricedotwho.mcprotocol.protocol.packet.play.server.ServerRespawnPacket;
//import com.ricedotwho.mcprotocol.protocol.packet.play.server.entity.ServerEntityMetadataPacket;
//import com.ricedotwho.mcprotocol.protocol.packet.play.server.world.ServerBlockChangePacket;
//import com.ricedotwho.mcprotocol.protocol.packet.play.server.world.ServerMultiBlockChangePacket;
//import com.ricedotwho.mcprotocol.protocol.packet.play.server.world.ServerPlaySoundPacket;
//import com.ricedotwho.zero.event.custom.events.ClientTickEvent;
//import com.ricedotwho.zero.module.setting.ModeSetting;
//import com.ricedotwho.zero.module.setting.TextSetting;
//import com.ricedotwho.zero.util.*;
//import com.ricedotwho.zero.event.custom.EventHandler;
//import com.ricedotwho.zero.event.custom.events.ServerTickEvent;
//import com.ricedotwho.zero.event.packet.PacketContext;
//import com.ricedotwho.zero.event.packet.PacketEvent;
//import com.ricedotwho.zero.module.Module;
//import com.ricedotwho.zero.module.setting.BooleanSetting;
//import com.ricedotwho.zero.module.setting.NumberSetting;
//import com.ricedotwho.zero.util.command.CommandBase;
//import com.ricedotwho.zero.util.rotation.Rot;
//import com.ricedotwho.zero.util.rotation.RotUtil;
//
//import java.util.Arrays;
//import java.util.List;
//
//public class I1 extends Module {
//    private final ModeSetting mode = new ModeSetting("Mode", "Server Tick", List.of("Server Tick", "Client Tick", "Interval", "State"));
//    private final NumberSetting initialDelay = new NumberSetting("Initial delay", 0, 0, 500, 5);
//    private final NumberSetting interval = new NumberSetting("Interval", 50, 0, 250, 5);
//    private final NumberSetting minDiff = new NumberSetting("Min tick diff", 10, 0, 50, 1);
//    private final BooleanSetting block = new BooleanSetting("Block click", false);
//    private final BooleanSetting autoClick = new BooleanSetting("Auto click", false);
//    private final BooleanSetting bigButton = new BooleanSetting("Use big start button", false);
//    private final BooleanSetting aura = new BooleanSetting("Aura", false);
//    private final NumberSetting serverDelay = new NumberSetting("Server Delay", 0, 0, 20, 1);
//    private final NumberSetting ticks = new NumberSetting("Ticks", 8, 0, 20, 1);
//    private final BooleanSetting preClick = new BooleanSetting("Pre-click", false);
//    private final BooleanSetting armourStand = new BooleanSetting("Use armour stand", false);
//    private final BooleanSetting clickingTimes = new BooleanSetting("Clicking times", false);
//    private final BooleanSetting clickedTimes = new BooleanSetting("Clicked times", false);
//    private final BooleanSetting silent = new BooleanSetting("Silent", false);
//    private final BooleanSetting doneTitle = new BooleanSetting("Done Title", false);
//    private final NumberSetting doneTitleStay = new NumberSetting("Title Stay", 40, 0, 100, 1);
//    private final TextSetting titleContent = new TextSetting("Title Content", "&aI1 Done!", false, false);
//
//    private final Position startButton = new Position(110, 121, 91);
//    private final Box startBoxNormal = new Box(111, 121.625, 91.6875, 110.875, 121.375, 91.3125);
//    private final Box startBoxBig = new Box(111, 122, 92, 110.875, 121, 91);
//    private final List<String> names = Arrays.asList("§cInactive", "§cDevice");
//
//    private long lastState = 0;
//    private long lastTick = 0;
//    private long startedAt = 0;
//    private boolean ended = false;
//    private boolean shouldBlock = false;
//    private Status status = Status.NONE;
//    private int clicksLeft = 0;
//    private int startTimer = 0;
//    private int p3Ticks = -1;
//    private Pos pos = new Pos(0, 0, 0);
//    private Rot rot = new Rot(0, 0);
//    private boolean waitingForSwing = false;
//    private boolean startButtonState = false;
//
//    public I1(MinecraftClient proxy) {
//        super("I1", proxy);
//        register(mode, initialDelay, interval, minDiff, block, autoClick, bigButton, aura,
//                serverDelay, ticks,
//                preClick, armourStand, clickingTimes, clickedTimes, silent,
//                doneTitle, doneTitleStay, titleContent
//                );
//
//        this.getProxy().getModule(Command.class).register("i1pos", new CommandBase("Player's position from the I1 module. Does not track the position outside of P2 - P3", "", args -> ChatUtil.prefix(this.getProxy(), "Position:" + this.pos.toNiceString())));
//        this.getProxy().getModule(Command.class).register("i1rot", new CommandBase("Player's rotation from the I1 module. Does not track the rotation outside of P2 - P3", "", args -> ChatUtil.prefix(this.getProxy(), "Rotation:" + this.rot.toPretty())));
//    }
//
//    @Override
//    public void onEnable() {
//        reset();
//    }
//
//    @Override
//    public void onDisable() {
//        killInterval();
//    }
//
//    private void reset() {
//        shouldBlock = false;
//        status = Status.NONE;
//        clicksLeft = 0;
//        startTimer = 0;
//        p3Ticks = -1;
//        ended = false;
//        waitingForSwing = false;
//        pos.set(0, 0, 0);
//        rot.setPitch(0);
//        rot.setPitch(0);
//        startedAt = 0;
//        startButtonState = false;
//        lastState = 0;
//        lastTick = 0;
//    }
//
//    public void start() {
//        ended = false;
//        clicksLeft = this.ticks.getValue().intValue();
//        startedAt = System.currentTimeMillis();
//        shouldBlock = false;
//
//        if (this.mode.is("State") && !this.preClick.getValue()) clickButton();
//        if (this.mode.is("Interval") && !this.preClick.getValue()) startInterval();
//    }
//
//    private void startInterval() {
//        TaskQueue.addRepeatingTask("I1ClientTicker", this::onInterval, this.initialDelay.getValue().intValue(), this.interval.getValue().intValue());
//    }
//
//    private void killInterval() {
//        TaskQueue.cancelTask("I1ClientTicker");
//    }
//
//    public void onTick(Source source) {
//        if (this.autoClick.getValue() && this.preClick.getValue() && startTimer <= 10 && startTimer > 0 && clicksLeft <= 0 && p3Ticks == -1) {
//            clicksLeft = 20;
//            if (this.mode.is("State")) clickButton();
//            else if (this.mode.is("Interval")) startInterval();
//        }
//
//        if (!this.mode.is(source.option)) return;
//
//        long now = System.currentTimeMillis();
//        if (now - lastTick < minDiff.getValue()) return;
//        lastTick = now;
//
//        if (clicksLeft > 0 && this.autoClick.getValue()) {
//            clickButton();
//        }
//
//        clicksLeft--;
//
//        if (clicksLeft == 0) {
//            if (this.block.getValue() && !this.silent.getValue() && atSS()) chat("§cBlocking clicks");
//            shouldBlock = true;
//            ended = true;
//            pos = new Pos(0, 0, 0);
//            rot = new Rot(0, 0);
//        }
//        else if (clicksLeft == -2 && this.doneTitle.getValue() && atSS()) {
//            String content = titleContent.getValue().replace("&", "§");
//            ChatUtil.sendTitle(this.getProxy(), Message.fromString(""), Message.fromString(content), 0, this.doneTitleStay.getValue().intValue(), 0);
//        }
//        else if (clicksLeft == -20) {
//            if (this.block.getValue() && !this.silent.getValue()) chat("§aAllowing clicks");
//            shouldBlock = false;
//            killInterval();
//        }
//    }
//
//    private void onStartWasClicked() {
//        if (!this.mode.is("State")) return;
//
//        long now = System.currentTimeMillis();
//        if (now - lastTick < minDiff.getValue()) return;
//        lastTick = now;
//
//        if (clicksLeft > 0 && this.autoClick.getValue()) {
//            clickButton();
//            clicksLeft--;
//        }
//
//        if (clicksLeft == 0) {
//            if (atSS()) {
//                if (this.block.getValue() && !this.silent.getValue()) chat("§cBlocking clicks");
//                if (this.doneTitle.getValue()) {
//                    String content = titleContent.getValue().replace("&", "§");
//                    ChatUtil.sendTitle(this.getProxy(), Message.fromString(""), Message.fromString(content), 0, this.doneTitleStay.getValue().intValue(), 0);
//                }
//            }
//            shouldBlock = true;
//            ended = true;
//            pos = new Pos(0, 0, 0);
//            rot = new Rot(0, 0);
//
//            TaskQueue.addTask("I1StateModeBWC", () -> {
//                        if (this.block.getValue() && !this.silent.getValue()) chat("§aAllowing clicks");
//                        shouldBlock = false;
//                    }, 1000);
//        }
//    }
//
//    private void clickButton() {
//        if (!this.getProxy().getArea().is(Island.Dungeon) || !atSS() || shouldBlock) return;
//        this.sendServer(new ClientPlayerActionPacket(PlayerAction.START_DIGGING, startButton, Face.NORTH));
//        this.sendServer(new ClientSwingArmPacket());
//        this.sendServer(new ClientPlayerActionPacket(PlayerAction.CANCEL_DIGGING, startButton, Face.BOTTOM));
//        if (this.clickingTimes.getValue()) {
//            chat("Clicking at " + this.p3Ticks, true);
//        }
//    }
//
//    private boolean atSS() {
//        Pos p = pos.add(0, 1.62, 0);
//        boolean inRange = Utils.calcDist(p.x(), p.y(), p.z(), startButton.getX(), startButton.getY(), startButton.getZ()) < 25.0;
//        return inRange && (this.aura.getValue() || lookingAtStart());
//    }
//
//    private boolean lookingAtStart() {
//        return RotUtil.rayIntersectsAABB(pos.add(0, 1.62, 0), RotUtil.getLookVector(rot), this.bigButton.getValue() ? startBoxBig : startBoxNormal, 5);
//    }
//
//    @PacketEvent(direction = Direction.CLIENTBOUND)
//    public void onChat(PacketContext<ServerChatPacket> ctx) {
//        if (!this.getProxy().getArea().is(Island.Dungeon)) return;
//        ServerChatPacket packet = ctx.getPacket();
//        packet.lazyDecode();
//
//        switch (packet.getMessage().getFullTextClean()) {
//            case "[BOSS] Goldor: Who dares trespass into my domain?":
//                if (!this.armourStand.getValue() && p3Ticks == -1) {
//                    start();
//                    p3Ticks = 0;
//                    status = Status.P3;
//                }
//                break;
//            case "[BOSS] Storm: I should have known that I stood no chance.":
//                if (!this.preClick.getValue()) shouldBlock = true;
//                ended = false;
//                break;
//                case "[BOSS] Storm: Pathetic Maxor, just like expected.":
//                status = Status.P2;
//                break;
//            case "[DS] Starting S1 on next tick":
//                ended = false;
//                if (!this.preClick.getValue()) shouldBlock = true;
//                if (this.preClick.getValue() && this.autoClick.getValue()) {
//                    clicksLeft = 60;
//                    if (this.mode.is("State")) TaskQueue.addTask("I1DSStatePreclick", this::clickButton, 150); // u need to update the pos for this to work...
//                    else if (this.mode.is("Interval")) startInterval();
//                }
//                status = Status.P3_STARTING;
//                break;
//            case "The Core entrance is opening!":
//                status = Status.NONE;
//                break;
//        }
//    }
//
//    @PacketEvent(direction = Direction.CLIENTBOUND)
//    public void onEntityMetadata(PacketContext<ServerEntityMetadataPacket> ctx) {
//        if (!this.getProxy().getArea().is(Island.Dungeon) || status != Status.P3_STARTING || !this.armourStand.getValue()) return;
//        ServerEntityMetadataPacket packet = ctx.getPacket();
//        packet.lazyDecode();
//
//        for (EntityMetadata meta : packet.getMetadata()) {
//            if (meta.getType() == MetadataType.STRING && names.contains((String) meta.getValue()) && p3Ticks == -1) {
//                status = Status.P3;
//                p3Ticks = 0;
//                if (this.serverDelay.getValue() == 0) {
//                    start();
//                }
//                return;
//            }
//        }
//    }
//
//    /// Start timer garbage
//    @PacketEvent(direction = Direction.CLIENTBOUND)
//    public void onSound(PacketContext<ServerPlaySoundPacket> ctx) {
//        if (!this.getProxy().getArea().is(Island.Dungeon) || status != Status.P2 || startTimer > 0) return;
//        ServerPlaySoundPacket packet = ctx.getPacket();
//        packet.lazyDecode();
//
//        if (packet.getVolume() == 15.0 && packet.getPitch() == 1.0 && packet.getSound() == GenericSound.WITHER_HURT) {
//            startTimer = 100;
//            status = Status.P3_STARTING;
//        }
//    }
//
//    @EventHandler
//    public void onServerTick(ServerTickEvent event) {
//        if (!this.getProxy().getArea().is(Island.Dungeon) || !Utils.equalsOneOf(status, Status.P3, Status.P3_STARTING)) return;
//
//        if (startTimer > 0) startTimer--;
//
//        if (p3Ticks >= 0) p3Ticks++;
//
//        if (this.serverDelay.getValue() > 0 && p3Ticks == this.serverDelay.getValue()) start();
//
//        if (this.mode.is("Server Tick", "State")) onTick(Source.SERVER);
//    }
//
////     won't work with blink
//    @EventHandler
//    public void onClientTick(ClientTickEvent event) {
//        if (!this.getProxy().getArea().is(Island.Dungeon) || !Utils.equalsOneOf(status, Status.P3, Status.P3_STARTING) || !this.mode.is("Client Tick")) return;
//        onTick(Source.CLIENT);
//    }
//
//    public void onInterval() {
//        if (!this.getProxy().getArea().is(Island.Dungeon) || !Utils.equalsOneOf(status, Status.P3, Status.P3_STARTING) || !this.mode.is("Interval")) return;
//        onTick(Source.INTERVAL);
//    }
//
//    @PacketEvent(direction = Direction.CLIENTBOUND)
//    public void onRespawn(PacketContext<ServerRespawnPacket> ctx) {
//        reset();
//    }
//
//    @PacketEvent(direction = Direction.CLIENTBOUND)
//    public void onLogin(PacketContext<LoginSuccessPacket> ctx) {
//        reset();
//    }
//
//    private boolean ignorePos() {
//        return !this.getProxy().getArea().is(Island.Dungeon) || status.equals(Status.NONE) || ended;
//    }
//
//    @PacketEvent(direction = Direction.SERVERBOUND)
//    public void onPosition(PacketContext<ClientPlayerPositionPacket> ctx) {
//        if (ignorePos()) return;
//        ClientPlayerPositionPacket packet = ctx.getPacket();
//        packet.lazyDecode();
//        pos.set(packet.getX(), packet.getY(), packet.getZ());
//    }
//
//    @PacketEvent(direction = Direction.SERVERBOUND)
//    public void onPositionRotation(PacketContext<ClientPlayerPositionRotationPacket> ctx) {
//        if (ignorePos()) return;
//        ClientPlayerPositionRotationPacket packet = ctx.getPacket();
//        packet.lazyDecode();
//        pos.set(packet.getX(), packet.getY(), packet.getZ());
//        rot.setPitch(packet.getPitch());
//        rot.setYaw(packet.getYaw());
//    }
//
//    @PacketEvent(direction = Direction.SERVERBOUND)
//    public void onRotation(PacketContext<ClientPlayerRotationPacket> ctx) {
//        if (ignorePos()) return;
//        ClientPlayerRotationPacket packet = ctx.getPacket();
//        packet.lazyDecode();
//        rot.setPitch(packet.getPitch());
//        rot.setYaw(packet.getYaw());
//    }
//
//    @PacketEvent(direction = Direction.CLIENTBOUND)
//    public void onBlockChange(PacketContext<ServerBlockChangePacket> ctx) {
//        if (this.getProxy().getArea().is(Island.Dungeon) && (this.clickedTimes.getValue() || this.mode.is("State"))) {
//            ServerBlockChangePacket packet = ctx.getPacket();
//            packet.lazyDecode();
//            onBlockChange(packet.getRecord());
//        }
//    }
//
//    @PacketEvent(direction = Direction.CLIENTBOUND)
//    public void onMultiBlockChange(PacketContext<ServerMultiBlockChangePacket> ctx) {
//        if (this.getProxy().getArea().is(Island.Dungeon) && (this.clickedTimes.getValue() || this.mode.is("State"))) {
//            ServerMultiBlockChangePacket packet = ctx.getPacket();
//            packet.lazyDecode();
//            onBlockChange(packet.getRecords());
//        }
//    }
//
//    public void onBlockChange(BlockChangeRecord ...records) {
//        for (BlockChangeRecord record : records) {
//            if (record.getPosition().equals(startButton) && record.getBlockId() == 77) { // 77 -> minecraft:stone_button
//                int data = record.getData();
//                boolean pressed = data > 8 && data < 14; // 0-5 unpressed, 9-13 pressed
//                if (startButtonState == pressed) {
//                    long now = System.currentTimeMillis();
//                    if (now - lastState > minDiff.getValue()) {
//                        if (this.clickedTimes.getValue()) chat("Clicked at " + this.p3Ticks, true);
//                        onStartWasClicked();
//                        lastState = now;
//                    }
//                }
//                startButtonState = pressed;
//                return;
//            }
//        }
//    }
//
//    // bwcc
//
//    @PacketEvent(direction = Direction.SERVERBOUND)
//    public void onBlockPlacement(PacketContext<ClientPlayerPlaceBlockPacket> ctx) {
//        if (this.getProxy().getArea().is(Island.Dungeon) && this.block.getValue() && shouldBlock) {
//            ClientPlayerPlaceBlockPacket packet = ctx.getPacket();
//            packet.lazyDecode();
//            if (packet.getPosition().equals(startButton)) {
//                ctx.setCancelled(true);
//                waitingForSwing = true;
//                TaskQueue.addTask("i1wfs" + System.currentTimeMillis(), () -> waitingForSwing = false, 5);
//                chat("§cBlocked placement", true);
//            }
//        }
//    }
//
//    @PacketEvent(direction = Direction.SERVERBOUND)
//    public void onArmAnimation(PacketContext<ClientSwingArmPacket> ctx) {
//        if (this.getProxy().getArea().is(Island.Dungeon) && this.block.getValue() && shouldBlock && waitingForSwing) {
//            waitingForSwing = false;
//            TaskQueue.cancelFirstStarts("i1wfs");
//            ctx.setCancelled(true);
//            chat("§cBlocked swing", true);
//        }
////        if (digging) {
////            ctx.setCancelled(true);
////            ChatUtil.chat(this.getProxy(), "Blocked swing");
////        }
//    }
//
////    @PacketEvent(direction = Direction.SERVERBOUND)
////    public void onInteract(PacketContext<ClientPlayerActionPacket> ctx) {
////        if (shouldBlock || digging) {
////            ClientPlayerActionPacket packet = ctx.getPacket();
////            packet.lazyDecode();
////            if (packet.getPosition().equals(startButton) && packet.getAction() == PlayerAction.START_DIGGING) {
////                digging = true;
////                ctx.setCancelled(true);
////                ChatUtil.chat(this.getProxy(), "Blocked dig");
////            }
////            else if (Utils.equalsOneOf(packet.getAction(), PlayerAction.CANCEL_DIGGING,  PlayerAction.FINISH_DIGGING)) {
////                if (digging) {
////                    ctx.setCancelled(true);
////                    ChatUtil.chatchat(this.getProxy(), "Blocked dig");
////                }
////                digging = false;
////            }
////        }
////    }
//
//    private void chat(String message) {
//        chat(message, false);
//    }
//
//    private void chat(String message, boolean time) {
//        if (this.silent.getValue()) return;
//        if (time) {
//            message += " (" + (startedAt == 0 ? System.currentTimeMillis() : System.currentTimeMillis() - startedAt) + ")";
//        }
//        ChatUtil.chat(this.getProxy(), message);
//    }
//
//    private enum Status {
//        NONE,
//        P2,
//        P3_STARTING,
//        P3
//    }
//
//    public enum Source {
//        SERVER("Server Tick"),
//        CLIENT("Client Tick"),
//        INTERVAL("Interval"),
//        STATE("State");
//
//        public final String option;
//        Source(String s) {
//            option = s;
//        }
//    }
//}
