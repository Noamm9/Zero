package com.ricedotwho.zero.module.impl;

import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
import com.ricedotwho.mcprotocol.protocol.net.registry.PacketDirection;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.ClientboundRespawnPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.inventory.ClientboundContainerClosePacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.inventory.ClientboundContainerSetSlotPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.inventory.ClientboundOpenScreenPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.ServerboundClientTickEndPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.inventory.ServerboundContainerButtonClickPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.inventory.ServerboundContainerClickPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.inventory.ServerboundContainerClosePacket;
import com.ricedotwho.mcprotocol.protocol.packet.login.clientbound.ClientboundLoginFinishedPacket;
import com.ricedotwho.zero.event.custom.EventHandler;
import com.ricedotwho.zero.event.custom.events.ModuleEvent;
import com.ricedotwho.zero.event.custom.events.ServerTickEvent;
import com.ricedotwho.zero.event.packet.PacketContext;
import com.ricedotwho.zero.event.packet.PacketEvent;
import com.ricedotwho.zero.module.Module;
import com.ricedotwho.zero.module.impl.task.TickTask;
import com.ricedotwho.zero.module.setting.BooleanSetting;
import com.ricedotwho.zero.module.setting.MultiSetting;
import com.ricedotwho.zero.module.setting.NumberSetting;
import com.ricedotwho.zero.util.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.geysermc.mcprotocollib.protocol.data.game.inventory.*;
import org.geysermc.mcprotocollib.protocol.data.game.item.HashedStack;
import org.geysermc.mcprotocollib.protocol.data.game.item.ItemStack;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponentTypes;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponents;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AutoTerms extends Module {

    private final NumberSetting firstClick = new NumberSetting("First Click", 450, 400, 500, 5);
    private final NumberSetting delay = new NumberSetting("Delay", 120, 0, 200, 5);
    private final NumberSetting random = new NumberSetting("Random", 0, 0, 150, 5);
    private final NumberSetting stutter = new NumberSetting("Stutter", 0, 0, 150, 5);
    private final NumberSetting stutterChance = new NumberSetting("Stutter Chance", 0, 0, 1, 0.01);
    private final NumberSetting timeout = new NumberSetting("Timeout", 1000, 0, 2000, 50);
    private final MultiSetting terminals = new MultiSetting("Terminals", List.of("Order", "Select", "Rubix", "Panes", "Starts With"), List.of("Order", "Select", "Rubix", "Panes", "Starts With"));
    private final BooleanSetting notP3 = new BooleanSetting("Not P3", false);
    private final BooleanSetting dev = new BooleanSetting("Logging", false);


    private static final Pattern swPattern = Pattern.compile("What starts with: '(\\w+)'?");
    private static final Pattern selPattern = Pattern.compile("Select all the ([\\w ]+) items!");

    private static final List<Integer> rubixAllowedSlots = Arrays.asList(12, 13, 14, 21, 22, 23, 30, 31, 32);
    private static final List<Integer> rubixOrder = Arrays.asList(Items.RED_STAINED_GLASS_PANE, Items.ORANGE_STAINED_GLASS_PANE, Items.YELLOW_STAINED_GLASS_PANE, Items.GREEN_STAINED_GLASS_PANE, Items.BLUE_STAINED_GLASS_PANE);
    private static final List<TextComponent> rubixNames = Arrays.asList(
            Component.text("Red").color(NamedTextColor.GREEN),
            Component.text("Orange").color(NamedTextColor.GREEN),
            Component.text("Yellow").color(NamedTextColor.GREEN),
            Component.text("Green").color(NamedTextColor.GREEN),
            Component.text("Blue").color(NamedTextColor.GREEN)
    );
    private static final List<Integer> selectAllowedSlots = Arrays.asList(
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    );

    private static final Map<String, String> replacements = new HashMap<>();
    static {
        replacements.put("light gray", "silver");
        replacements.put("wool", "white");
        replacements.put("bone", "white");
        replacements.put("ink", "black");
        replacements.put("lapis", "blue");
        replacements.put("cocoa", "brown");
        replacements.put("dandelion", "yellow");
        replacements.put("rose", "red");
        replacements.put("cactus", "green");
    }

    private Container container;
    private Container prevContainer;

    private int ticks = 0;

    private String extra = "";
    private long lastClick = System.currentTimeMillis();
    private TerminalType type = TerminalType.NONE;
    private boolean clicking = false;

    private boolean setSlot = false;
    private boolean serverClose = false;
    private boolean clientClose = false;
    private boolean inP3 = false;

    private boolean stuttered = false;
    private TermSol current = null;

    public AutoTerms(MinecraftClient proxy) {
        super("AutoTerms", proxy);
        register(
                firstClick,
                delay,
                random,
                stutter,
                timeout,
                terminals,
                notP3,
                dev
        );
    }

    @Override
    public void onEnable() {
        if (this.zptEnabled()) {
            this.compatWarning();
        }
        this.reset();
    }

    private boolean zptEnabled() {
        return this.getProxy().getModule(ZeroPingTerms.class).isEnabled();
    }

    @EventHandler
    public void onModule(ModuleEvent.Enabled event) {
        if (event.getModule().getClass() == ZeroPingTerms.class) {
            compatWarning();
        }
    }

    private void compatWarning() {
        ChatUtil.prefix(this.getProxy(),
                Component.text("Warning: ").color(NamedTextColor.DARK_RED)
                        .append(Component.text("AutoTerms is not compatible with ZeroPingTerms! please disable ZeroPingTerms or AutoTerms ").color(NamedTextColor.RED))
                        .append(Component.text("WILL NOT WORK!").color(NamedTextColor.DARK_RED)));
    }

    private void reset() {
        this.resetWindow();
        this.resetPrevWindow();
        this.stuttered = false;
        this.current = null;
        this.serverClose = false;
        this.clientClose = false;
        TaskQueue.cancelAllStarts("AutoTermsTimeout");
        this.ticks = 0;
    }

    private void resetWindow() {
        this.setSlot = false;
        this.type = TerminalType.NONE;
        this.extra = "";
        this.container = null;
    }

    private void resetPrevWindow() {
        prevContainer = null;
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onChat(PacketContext<ClientboundSystemChatPacket> ctx) {
        if (!this.getProxy().getArea().is(Island.Dungeon)) return;
        ClientboundSystemChatPacket packet = ctx.getPacket();
        packet.lazyDecode();
        if (packet.isOverlay()) return;
        String message = ChatUtil.stripFormatting(ChatUtil.getContent(packet.getContent()));
        if ("[BOSS] Goldor: Who dares trespass into my domain?".equals(message)) {
            reset();
            this.inP3 = true;
        } else if ("[BOSS] Goldor: You have done it, you destroyed the factory…".equals(message) || "The Core entrance is opening!".equals(message)) {
            this.inP3 = false;
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

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onServerClose(PacketContext<ClientboundContainerClosePacket> ctx) {
        if (!this.getProxy().getArea().is(Island.Dungeon) || !serverClose) return;
        reset();
    }

    @PacketEvent(direction = PacketDirection.SERVERBOUND)
    public void onClientClose(PacketContext<ServerboundContainerClosePacket> ctx) {
        if (!this.getProxy().getArea().is(Island.Dungeon) || !clientClose) return;
        reset();
    }

    @PacketEvent(direction = PacketDirection.SERVERBOUND)
    public void onContainerClick(PacketContext<ServerboundContainerClickPacket> ctx) {
        if (!this.getProxy().getArea().is(Island.Dungeon) || !this.clientClose || zptEnabled()) return;
        ctx.setCancelled(true);
    }

    @PacketEvent(direction = PacketDirection.SERVERBOUND)
    public void onButtonClick(PacketContext<ServerboundContainerButtonClickPacket> ctx) {
        if (!this.getProxy().getArea().is(Island.Dungeon) || !this.clientClose || zptEnabled()) return;
        ctx.setCancelled(true);
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onWindow(PacketContext<ClientboundOpenScreenPacket> ctx) {
        if (!this.getProxy().getArea().is(Island.Dungeon) || !inP3 && !notP3.getValue() || zptEnabled()) return;
        ClientboundOpenScreenPacket packet = ctx.getPacket();
        packet.lazyDecode();
        resetWindow();

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

        this.container = Container.create(packet.getContainerId(), packet.getType(), name);
        if (this.container == null || this.container.windowId < 1 || this.container.windowId > 100) {
            reset();
            return;
        }
        this.type = TerminalType.findByStartsWithGuiName(name);

        switch (type) {
            case PANES -> {
                if (!terminals.get("Panes")) return;
            }
            case RUBIX -> {
                if (!terminals.get("Rubix")) return;
            }
            case ORDER -> {
                if (!terminals.get("Order")) return;
            }
            case STARTS_WITH -> {
                if (!terminals.get("Starts With")) return;
                Matcher matcher = swPattern.matcher(name);
                if (matcher.find()) {
                    this.extra = matcher.group(1).toLowerCase();
                } else {
                    ChatUtil.prefix(this.getProxy(), Component.text("AutoTerms: ").color(NamedTextColor.RED).append(Component.text("Failed to find letter!").color(NamedTextColor.DARK_RED)));
                    this.extra = "";
                }
            }
            case SELECT -> {
                if (!terminals.get("Select")) return;
                Matcher matcher = selPattern.matcher(name);
                if (matcher.find()) {
                    this.extra = matcher.group(1).toLowerCase();
                } else {
                    ChatUtil.prefix(this.getProxy(), Component.text("AutoTerms: ").color(NamedTextColor.RED).append(Component.text("Failed to find colour!").color(NamedTextColor.DARK_RED)));
                    this.extra = "";
                }
            }
            default -> {
                reset();
                return;
            }
        }

        TaskQueue.cancelTask("AutoTermsTimeout" + (this.container.windowId - 1 < 1 ? 100 : this.container.windowId - 1));

        this.setSlot = true;
        this.serverClose = true;
        this.clientClose = true;
    }

    @EventHandler
    public void onServerTick(ServerTickEvent event) {
        ticks++;
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onSetSlot(PacketContext<ClientboundContainerSetSlotPacket> ctx) {
        if (!this.getProxy().getArea().is(Island.Dungeon) || !this.setSlot || !this.inP3 && !this.notP3.getValue() || this.zptEnabled()) return;
        ClientboundContainerSetSlotPacket packet = ctx.getPacket();
        packet.lazyDecode();
        if (this.container.windowId != packet.getContainerId()) return;
        int slot = packet.getSlot();
        this.container.setSlot(slot, packet.getStateId(), packet.getItem());
        if (!this.container.isFull() || this.clicking) return;
        this.setSlot = false;

        boolean isNewTerminal = !this.container.easyEquals(prevContainer);

        resetPrevWindow();
        int wid = this.container.windowId;

        // first click
        long now = System.currentTimeMillis();
        this.clicking = true;
        if (isNewTerminal) {
            log("new terminal: " + type + " wid: " + wid);
            this.lastClick = now;
            long millis = this.firstClick.getValue().longValue();
            int ticks = Math.toIntExact(millis / 50);
            Scheduler.schedule("AutoTermsFirstClick-" + this.getProxy().getSession().getProfile().getId(), millis, ticks,
                    () -> {
                        log("clicking after " + (System.currentTimeMillis() - this.lastClick) + "ms (" + this.firstClick.getValue().longValue() + "ms, " + this.ticks + " ticks)");
                        this.prepareClick(wid);
                        this.clicking = false;
            });
        } else {
            // normal click
            long d = getDelay();
            long millis = Math.max(d - (now - this.lastClick), 0);
            int ticks = Math.max(0, Math.max((this.delay.getValue().intValue() / 50) - this.ticks, Math.toIntExact(d) / 50));
            Scheduler.schedule("AutoTermsClick-" + this.getProxy().getSession().getProfile().getId(), millis, ticks,
                    () -> {
                        log("clicking after " + (System.currentTimeMillis() - this.lastClick) + "ms, " + this.ticks + " ticks (" + millis + "ms, " + ticks + " ticks)");
                        this.prepareClick(wid);
                        this.clicking = false;
            });
        }
    }

    private long getDelay() {
        long delay = (long) (this.delay.getValue().longValue() + Math.floor(Math.random() * (this.random.getValue().longValue() + 1)));
        if (!this.stuttered && Math.random() < this.stutterChance.getValue()) {
            delay += this.stutter.getValue();
            this.stuttered = true;
        }
        return delay;
    }

    private void prepareClick(int wid) {
        if (this.container == null) {
            log("Container is null?");
            return;
        }
        if (this.container.windowId != wid) {
            ChatUtil.prefix(this.getProxy(), "Window ID mismatch! current: " + this.container.windowId + " tried to click: " + wid);
            return;
        }
        TermSol click = getNextClick();
        if (click == null || click.slot() < 0 || click.slot() > this.container.windowSize) return;

        this.lastClick = System.currentTimeMillis();
        this.ticks = 0;
        click(click, wid);
    }

    private void click(TermSol click, int wid) {
        if (this.container.windowId != wid) return;
        resetPrevWindow();
        this.prevContainer = Container.create(wid % 100 + 1, this.container.type, getPrediction(click), this.container.title);

        ContainerActionType action = click.clickType() == CreativeGrabAction.GRAB ? ContainerActionType.CREATIVE_GRAB_MAX_STACK : ContainerActionType.CLICK_ITEM;

        Int2ObjectMap<HashedStack> map = this.container.simulateClick(click.slot(), click.clickType(), action);

        getProxy().getRemoteSession().send(
                new ServerboundContainerClickPacket(
                        wid,
                        this.container.stateId,
                        click.slot(),
                        action,
                        click.clickType(),
                        this.container.getCarriedHash(),
                        map
                )
        );

        if (this.timeout.getValue() > 0) TaskQueue.addTask("AutoTermsTimeout" + wid, () -> prepareClick(wid), this.timeout.getValue().longValue());
    }

    private TermSol getNextClick() {
        List<TermSol> solution = getSolution();

        if (solution.isEmpty()) {
            ChatUtil.prefix(this.getProxy(), "Empty solution for " + type + "!");
            return null;
        }

        if (this.type.equals(TerminalType.ORDER)) {
            return solution.get(0);
        }

        if (this.current == null) {
            this.current = getRandom(solution);
        } else {
            TermSol last = this.current;
            this.current = solution.stream()
                    .reduce(solution.get(0),
                            (best, idx) ->
                                    distance(idx.slot(), last.slot()) < distance(best.slot(), last.slot()) ? idx : best);
        }
        return this.current;
    }

    private Pair<Integer, Integer> toRC(int index) {
        return new Pair<>(
                (int) Math.floor(index / 9f),
                index % 9
        );
    }

    private int distance(int a, int b) {
        Pair<Integer, Integer> A = this.toRC(a);
        Pair<Integer, Integer> B = this.toRC(b);
        return (int) (Math.pow(A.getFirst() - B.getFirst(), 2) + Math.pow(A.getSecond() - B.getSecond(), 2));
    }

    private <T> T getRandom(List<T> list) {
        return list.get((int) Math.floor(Math.random() * list.size()));
    }

    private List<TermSol> getSolution() {
        return switch (type) {
            case STARTS_WITH -> solveStartsWith();
            case SELECT -> solveSelect();
            case RUBIX -> solveRubix();
            case PANES -> solvePanes();
            case ORDER -> solveOrder();
            default -> List.of();
        };
    }

    private List<TermSol> solvePanes() {
        return this.container.container.entrySet().stream()
                .filter(e -> e != null && e.getValue() != null && e.getValue().getId() == Items.RED_STAINED_GLASS_PANE)
                .map(e -> new TermSol(e.getKey()))
                .toList();
    }

    public List<TermSol> solveRubix() {
        List<TermSol> solution = new ArrayList<>();

        int[] clicks = new int[5];

        for (int i = 0; i < 5; i++) {
            int idx = i;

            this.container.container.forEach((slot, stack) -> {
                if (stack == null) return;
                if (!rubixAllowedSlots.contains(slot)) return;
                if (stack.getId() == rubixOrder.get(calcRubixIndex(idx))) return;

                int id = stack.getId();

                if (id == rubixOrder.get(calcRubixIndex(idx - 1))
                        || id == rubixOrder.get(calcRubixIndex(idx + 1))) {
                    clicks[idx] += 1;
                } else if (id == rubixOrder.get(calcRubixIndex(idx - 2))
                        || id == rubixOrder.get(calcRubixIndex(idx + 2))) {
                    clicks[idx] += 2;
                }
            });
        }

        int origin = 0;
        for (int i = 1; i < clicks.length; i++) {
            if (clicks[i] < clicks[origin]) {
                origin = i;
            }
        }

        for (Map.Entry<Integer, ItemStack> entry : this.container.container.entrySet()) {
            int slot = entry.getKey();
            ItemStack stack = entry.getValue();

            if (stack == null) continue;
            if (!rubixAllowedSlots.contains(slot)) continue;
            if (stack.getId() == rubixOrder.get(calcRubixIndex(origin))) continue;

            int id = stack.getId();

            if (id == rubixOrder.get(calcRubixIndex(origin - 2))) {
                solution.add(new TermSol(slot, CreativeGrabAction.GRAB));
                solution.add(new TermSol(slot, CreativeGrabAction.GRAB));
            } else if (id == rubixOrder.get(calcRubixIndex(origin - 1))) {
                solution.add(new TermSol(slot, CreativeGrabAction.GRAB));
            } else if (id == rubixOrder.get(calcRubixIndex(origin + 1))) {
                solution.add(new TermSol(slot, ClickItemAction.RIGHT_CLICK));
            } else if (id == rubixOrder.get(calcRubixIndex(origin + 2))) {
                solution.add(new TermSol(slot, ClickItemAction.RIGHT_CLICK));
                solution.add(new TermSol(slot, ClickItemAction.RIGHT_CLICK));
            }
        }

        return solution;
    }

    private int calcRubixIndex(int index) {
        return (index + rubixOrder.size()) % rubixOrder.size();
    }

    private List<TermSol> solveOrder() {
        return this.container.container.entrySet().stream()
                .filter(e -> e != null && e.getValue() != null
                        && e.getValue().getId() == Items.RED_STAINED_GLASS_PANE)
                    .sorted(Comparator.comparingInt(item -> item.getValue().getAmount()))
                .map(e -> new TermSol(e.getKey()))
                .toList();
    }

    private List<TermSol> solveStartsWith() {
        return this.container.container.entrySet().stream()
                .filter(e -> {
                            if (e != null && e.getValue() != null && e.getValue().getId() > 0) {
                                if (Utils.isEnchanted(e.getValue())) return false;
                                Component name = Utils.getCustomName(e.getValue());
                                String fullName = ChatUtil.stripFormatting(ChatUtil.getContent(name));
                                return fullName.toLowerCase().startsWith(this.extra);
                            }
                            return false;
                        }
                )
                .map(e -> new TermSol(e.getKey()))
                .toList();
    }

    private List<TermSol> solveSelect() {
        List<TermSol> result = new ArrayList<>();

        for (Map.Entry<Integer, ItemStack> entry : this.container.container.entrySet()) {
            int slot = entry.getKey();
            ItemStack stack = entry.getValue();
            if (stack == null || !selectAllowedSlots.contains(slot) || Utils.isEnchanted(entry.getValue())) continue;
            Component customName = Utils.getCustomName(entry.getValue());
            String fullName = ChatUtil.stripFormatting(ChatUtil.getContent(customName)).toLowerCase();
            String name = fixName(fullName);
            if (!name.startsWith(this.extra)) continue;
            result.add(new TermSol(slot));
        }

        return result;
    }

    private String fixName(String name) {
        for (Map.Entry<String, String> e : replacements.entrySet()) {
            if (name.startsWith(e.getKey())) {
                name = e.getValue() + name.substring(e.getKey().length());
            }
        }
        return name;
    }

    private Map<Integer, ItemStack> getPrediction(TermSol sol) {
        Map<Integer, ItemStack> prediction = this.container.container.entrySet().stream().map(e -> Map.entry(e.getKey(), new ItemStack(e.getValue().getId(), e.getValue().getAmount(), e.getValue().getDataComponentsPatch() == null ? null : e.getValue().getDataComponentsPatch().clone()))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return switch (this.type) {
            case PANES -> {
                ItemStack stack = prediction.get(sol.slot());
                DataComponents data = stack.getDataComponentsPatch().clone();
                ItemStack newStack = new ItemStack(Items.LIME_STAINED_GLASS_PANE, 1, data);
                data.put(DataComponentTypes.CUSTOM_NAME, Utils.getBlank().append(Component.text("On").color(NamedTextColor.GREEN)));
                prediction.put(sol.slot(), newStack);
                yield prediction;
            }
            case RUBIX -> {
                ItemStack stack = prediction.get(sol.slot());
                int offset = sol.clickType() == CreativeGrabAction.GRAB ? 1 : -1;
                int index = rubixOrder.indexOf(stack.getId());
                int newIndex = calcRubixIndex(index + offset);
                DataComponents data = stack.getDataComponentsPatch().clone();
                ItemStack newStack = new ItemStack(rubixOrder.get(newIndex), 1, data);
                data.put(DataComponentTypes.CUSTOM_NAME, Utils.getBlank().append(rubixNames.get(newIndex)));
                prediction.put(sol.slot(), newStack);
                yield prediction;
            }
            case ORDER -> {
                ItemStack stack =  prediction.get(sol.slot());
                ItemStack newStack = new ItemStack(Items.LIME_STAINED_GLASS_PANE, stack.getAmount(), stack.getDataComponentsPatch());
                prediction.put(sol.slot(), newStack);
                yield prediction;
            }
            case SELECT, STARTS_WITH -> {
                ItemStack stack = prediction.get(sol.slot());
                if (stack.getDataComponentsPatch() != null) {
                    stack.getDataComponentsPatch().put(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
                }
                yield prediction;
            }
            default -> new HashMap<>();
        };
    }

    public record TermSol(int slot, ContainerAction clickType) {
            public TermSol(int slot) {
                this(slot, CreativeGrabAction.GRAB);
            }
        }

    @Getter
    public enum TerminalType {
        PANES("Correct all the panes!"),
        RUBIX("Change all to same color!"),
        ORDER("Click in order!"),
        STARTS_WITH("What starts with:"),
        SELECT("Select all the"),
        MELODY("Click the button on time!"),
        NONE("None");

        private final String guiName;

        TerminalType(String guiName) {
            this.guiName = guiName;
        }

        public static TerminalType findByStartsWithGuiName(String name) {
            return Arrays.stream(TerminalType.values())
                    .filter(type -> name.startsWith(type.getGuiName()))
                    .findFirst()
                    .orElse(TerminalType.NONE);
        }
    }

    private void log(String message) {
        if (this.dev.getValue()) ChatUtil.prefix(this.getProxy(), message + " (" + System.currentTimeMillis() + ")");
    }

    private record QueuedClick(TermSol sol, int wId) {}
}
