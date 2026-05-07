package com.ricedotwho.zero.module.impl;

import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
import com.ricedotwho.mcprotocol.protocol.net.registry.PacketDirection;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.ClientboundRespawnPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.inventory.ClientboundContainerClosePacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.inventory.ClientboundContainerSetSlotPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.inventory.ClientboundOpenScreenPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.inventory.ServerboundContainerClickPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.inventory.ServerboundContainerClosePacket;
import com.ricedotwho.mcprotocol.protocol.packet.login.clientbound.ClientboundLoginFinishedPacket;
import com.ricedotwho.zero.event.custom.EventHandler;
import com.ricedotwho.zero.event.custom.events.ModuleEvent;
import com.ricedotwho.zero.event.packet.PacketContext;
import com.ricedotwho.zero.event.packet.PacketEvent;
import com.ricedotwho.zero.module.Module;
import com.ricedotwho.zero.module.setting.BooleanSetting;
import com.ricedotwho.zero.module.setting.MultiSetting;
import com.ricedotwho.zero.module.setting.NumberSetting;
import com.ricedotwho.zero.util.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
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

public class ReAutoTerms extends Module {

    private final NumberSetting firstClick = new NumberSetting("First Click", 450, 400, 600, 10);
    private final NumberSetting delay = new NumberSetting("Delay", 110, 90, 400, 10);
    private final NumberSetting minDelay = new NumberSetting("Min Delay", 100, 0, 200, 10);
    private final NumberSetting timeout = new NumberSetting("Timeout", 1000, 0, 3000, 100);
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

    private String extra = "";
    private long lastClick = System.currentTimeMillis();
    private TerminalType type = TerminalType.NONE;

    private boolean setSlot = false;
    private boolean inP3 = false;

    public ReAutoTerms(MinecraftClient proxy) {
        super("ReAutoTerms", proxy);
        register(
                firstClick,
                delay,
                minDelay,
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
                        .append(Component.text("ReAutoTerms is not compatible with ZeroPingTerms! please disable ZeroPingTerms or ReAutoTerms ").color(NamedTextColor.RED))
                        .append(Component.text("WILL NOT WORK!").color(NamedTextColor.DARK_RED)));
    }

    private void reset() {
        this.resetWindow();
        this.resetPrevWindow();
        TaskQueue.cancelAllStarts("ReAutoTermsTimeout");
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
        if (!this.getProxy().getArea().is(Island.Dungeon)) return;
        reset();
    }

    @PacketEvent(direction = PacketDirection.SERVERBOUND)
    public void onClientClose(PacketContext<ServerboundContainerClosePacket> ctx) {
        if (!this.getProxy().getArea().is(Island.Dungeon)) return;
        reset();
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

        log("Window opened: " + name);

        this.container = Container.create(packet.getContainerId(), packet.getType(), name);
        if (this.container == null || this.container.windowId < 1 || this.container.windowId > 100) {
            log("Invalid container or windowId: " + (this.container == null ? "null" : this.container.windowId));
            reset();
            return;
        }
        this.type = TerminalType.findByStartsWithGuiName(name);

        log("Terminal type: " + type + ", wid: " + this.container.windowId);

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
                    log("Starts With letter: " + this.extra);
                } else {
                    this.extra = "";
                    log("Failed to find letter for Starts With");
                }
            }
            case SELECT -> {
                if (!terminals.get("Select")) return;
                Matcher matcher = selPattern.matcher(name);
                if (matcher.find()) {
                    this.extra = matcher.group(1).toLowerCase();
                    log("Select color: " + this.extra);
                } else {
                    this.extra = "";
                    log("Failed to find color for Select");
                }
            }
            default -> {
                log("Unknown terminal type, resetting");
                reset();
                return;
            }
        }

        TaskQueue.cancelTask("ReAutoTermsTimeout" + (this.container.windowId - 1 < 1 ? 100 : this.container.windowId - 1));
        TaskQueue.cancelTask("ReAutoTermsFirstClick" + (this.container.windowId - 1 < 1 ? 100 : this.container.windowId - 1));
        TaskQueue.cancelTask("ReAutoTermsClick" + (this.container.windowId - 1 < 1 ? 100 : this.container.windowId - 1));

        this.setSlot = true;
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onSetSlot(PacketContext<ClientboundContainerSetSlotPacket> ctx) {
        if (!this.getProxy().getArea().is(Island.Dungeon) || !this.setSlot || !this.inP3 && !this.notP3.getValue() || this.zptEnabled()) return;
        ClientboundContainerSetSlotPacket packet = ctx.getPacket();
        packet.lazyDecode();
        if (this.container.windowId != packet.getContainerId()) return;
        int slot = packet.getSlot();
        this.container.setSlot(slot, packet.getStateId(), packet.getItem());
        if (!this.container.isFull()) return;
        this.setSlot = false;

        boolean isNewTerminal = !this.container.easyEquals(prevContainer);

        resetPrevWindow();
        int wid = this.container.windowId;

        log("Container full, isNewTerminal: " + isNewTerminal + ", wid: " + wid);

        if (isNewTerminal) {
            long millis = this.firstClick.getValue().longValue();
            log("Scheduling first click in " + millis + "ms");
            TaskQueue.addTask("ReAutoTermsFirstClick" + wid, () -> {
                prepareClick(wid);
            }, millis);
        } else {
            long now = System.currentTimeMillis();
            long calculatedDelay = delay.getValue().longValue() - (now - this.lastClick);
            long minDelayValue = this.minDelay.getValue().longValue();
            if (calculatedDelay < minDelayValue) calculatedDelay = minDelayValue;
            log("Calculated delay: " + calculatedDelay + "ms (delay setting: " + delay.getValue() + "ms, time since last: " + (now - this.lastClick) + "ms, min: " + minDelayValue + "ms)");
            if (calculatedDelay > 0) {
                log("Scheduling click in " + calculatedDelay + "ms");
                TaskQueue.addTask("ReAutoTermsClick" + wid, () -> {
                    prepareClick(wid);
                }, calculatedDelay);
            } else {
                log("Executing click immediately");
                prepareClick(wid);
            }
        }
    }

    private void prepareClick(int wid) {
        if (this.container == null) {
            log("prepareClick: container is null");
            return;
        }
        if (this.container.windowId != wid) {
            log("prepareClick: window ID mismatch (current: " + this.container.windowId + ", expected: " + wid + ")");
            return;
        }
        TermSol click = getNextClick();
        if (click == null || click.slot() < 0 || click.slot() > this.container.windowSize) {
            log("prepareClick: invalid click (null or out of bounds)");
            return;
        }

        long now = System.currentTimeMillis();
        long timeSinceLast = now - this.lastClick;
        this.lastClick = now;
        log("Clicking slot " + click.slot() + " (" + timeSinceLast + "ms since last click)");
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

        if (this.timeout.getValue() > 0) TaskQueue.addTask("ReAutoTermsTimeout" + wid, () -> prepareClick(wid), this.timeout.getValue().longValue());
    }

    private TermSol getNextClick() {
        List<TermSol> solution = getSolution();

        log("Solution size: " + solution.size());

        if (solution.isEmpty()) {
            return null;
        }

        if (this.type.equals(TerminalType.ORDER)) {
            return solution.get(0);
        }

        return solution.get((int) Math.floor(Math.random() * solution.size()));
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
        List<Integer> allowedSlots = Arrays.asList(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34);
        return this.container.container.entrySet().stream()
                .filter(e -> {
                            if (e != null && e.getValue() != null && e.getValue().getId() > 0) {
                                if (!allowedSlots.contains(e.getKey())) return false;
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
                ItemStack stack = prediction.get(sol.slot());
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
                    .filter(type -> name.startsWith(type.guiName))
                    .findFirst()
                    .orElse(TerminalType.NONE);
        }
    }

    private void log(String message) {
        if (this.dev.getValue()) ChatUtil.prefix(this.getProxy(), message + " (" + System.currentTimeMillis() + ")");
    }
}
