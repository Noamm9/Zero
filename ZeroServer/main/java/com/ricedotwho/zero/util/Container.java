package com.ricedotwho.zero.util;

import com.ricedotwho.zero.Zero;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.geysermc.mcprotocollib.protocol.data.game.inventory.ClickItemAction;
import org.geysermc.mcprotocollib.protocol.data.game.inventory.ContainerAction;
import org.geysermc.mcprotocollib.protocol.data.game.inventory.ContainerActionType;
import org.geysermc.mcprotocollib.protocol.data.game.inventory.ContainerType;
import org.geysermc.mcprotocollib.protocol.data.game.item.HashedStack;
import org.geysermc.mcprotocollib.protocol.data.game.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Container {
    public final ContainerType type;
    public final int windowId;
    public final int windowSize;
    public final Map<Integer, ItemStack> container;
    public int stateId = 0;
    @NotNull
    public ItemStack carried = new ItemStack(0);
    public final String title;

    private Container(int windowId,  ContainerType type, int windowSize, Map<Integer, ItemStack> map, String title) {
        this.windowId = windowId;
        this.type = type;
        this.windowSize = windowSize;
        this.container = map;
        this.title = title;
    }

    public static Container create(int windowId, ContainerType type, String title) {
        int size = Utils.getGuiSlotCount(type);
        if (size == -1) return null;
        return new Container(windowId, type, size, new HashMap<>(), title);
    }

    public static Container create(int windowId, ContainerType type, Map<Integer, ItemStack> items, String title) {
        int size = Utils.getGuiSlotCount(type);
        if (size == -1) return null;
        return new Container(windowId, type, size, items, title);
    }

    public void setSlot(int slot, int stateId, ItemStack item) {
        if (slot >= 0) this.container.put(slot, item);
        this.stateId = stateId;
    }

    public long getRealSize() {
        return container.values().stream().filter(item -> item != null && item.getId() != 0).count();
    }

    public boolean isFull() {
        return getRealSize() == windowSize - 1;
    }

    public void setCarried(ItemStack item) {
        carried = item == null ? new ItemStack(0) : item;
    }

    public HashedStack getCarriedHash() {
        return Utils.toHashedStack(this.carried);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Container other)) return false;
        if (other == this) return true;
        if (other.windowId != this.windowId || other.windowSize != this.windowSize) return false;

        for (int i = 0; i < windowSize; i++) {
            ItemStack a = this.container.get(i);
            ItemStack b = other.container.get(i);
            if (!Utils.itemsEqual(a, b)) {
                Zero.getLogger().info("Unequal item stacks. a: {}, b: {}", a, b);
                return false;
            }
        }
        return true;
    }

    public boolean easyEquals(@Nullable Container other) {
        if (other == this) return true;
        if (other == null || other.windowId != this.windowId || other.windowSize != this.windowSize) return false;

        for (int i = 0; i < windowSize; i++) {
            ItemStack a = this.container.get(i);
            ItemStack b = other.container.get(i);
            if (a == b) continue;
            if (a == null || b == null || a.getId() != b.getId() || a.getAmount() != b.getAmount() || (Utils.isEnchanted(a) || Utils.isEnchantable(a)) != (Utils.isEnchanted(b) || Utils.isEnchantable(b))) {
                return false;
            }
        }
        return true;
    }

    public Int2ObjectMap<HashedStack> simulateClick(int slot, ContainerAction param, ContainerActionType action) {
        Map<Integer, ItemStack> container2 = new HashMap<>();

        for (Map.Entry<Integer, ItemStack> entry : this.container.entrySet()) {
            container2.put(entry.getKey(), entry.getValue() == null ? new ItemStack(0) : new ItemStack(entry.getValue().getId(), entry.getValue().getAmount(), entry.getValue().getDataComponentsPatch()));
        }

        clicked(slot, param, action);

        Int2ObjectMap<HashedStack> int2ObjectMap = new Int2ObjectOpenHashMap<>();

        for (int i = 0; i < this.windowSize; i++) {
            ItemStack stack = this.container.get(i);
            if (stack == null) stack = new ItemStack(0);
            ItemStack stack2 = container2.get(i);
            if (stack2 == null) stack2 = new ItemStack(0);
            if (!matches(stack, stack2)) {
                int2ObjectMap.put(i, Utils.toHashedStack(stack));
            }
        }

        return int2ObjectMap;
    }

    public void clicked(int slot, ContainerAction param, ContainerActionType action) {
        // we only care about left, right, and clone

        switch (action) {
            case CLICK_ITEM -> {
                if (slot == -999) {
                    if (!Utils.isEmpty(carried)) {
                        if (param == ClickItemAction.LEFT_CLICK) {
                            carried = new ItemStack(0);
                        } else {
                            if (carried.getAmount() == 1) {
                                carried = new ItemStack(0);
                            } else {
                                carried = new ItemStack(carried.getId(), carried.getAmount() - 1, carried.getDataComponentsPatch());
                            }
                        }
                    }
                } else {
                    ItemStack carried = this.carried;
                    ItemStack stack = this.container.get(slot);

                    if (Utils.isEmpty(stack)) {
                        if (!Utils.isEmpty(carried)) {
                            int q = param == ClickItemAction.LEFT_CLICK ? carried.getAmount() : 1;
                            this.carried = safeInsert(carried, q, stack, slot);
                        }
                    }
                    else {
                        if (Utils.isEmpty(carried)) {
                            if (!Utils.isEmpty(stack)) {
                                this.carried = stack;
                                container.put(slot, new ItemStack(0));
                            }
                        }
                        else if (mayPlace(stack, carried)) {
                            if (stack != null && carried.getDataComponentsPatch() == stack.getDataComponentsPatch()) {
                                int q = param == ClickItemAction.LEFT_CLICK ? carried.getAmount() : 1;
                                this.carried = safeInsert(carried, q, stack, slot);
                            } else if (this.carried.getAmount() < 64) {
                                this.carried = stack == null ? new ItemStack(0) : stack;
                            }
                        } else if (stack != null && carried.getDataComponentsPatch() == stack.getDataComponentsPatch()) {
                            if (!Utils.isEmpty(stack) && stack.getAmount() < 64) {
                                this.carried = new ItemStack(carried.getId(), carried.getAmount() + stack.getAmount(), carried.getDataComponentsPatch());
                                container.put(slot, new ItemStack(0));
                            }
                        }
                    }
                }
            }
            case CREATIVE_GRAB_MAX_STACK -> {
                // nothing
            }
        }
    }

    public boolean matches(ItemStack a, ItemStack b) {
        if (a == b) return true;
        return a.getId() == b.getId() && a.getAmount() == b.getAmount() && a.getDataComponentsPatch() == b.getDataComponentsPatch();
    }

    public ItemStack safeInsert(ItemStack to, int amount, ItemStack curr, int slot) {
        if (!Utils.isEmpty(to) && mayPlace(to, curr)) {
            if (Utils.isEmpty(to) && !Utils.isEmpty(curr)) {
                container.put(slot, new ItemStack(curr.getId(), curr.getAmount() / 2, curr.getDataComponentsPatch()));
                return new ItemStack(curr.getId(), curr.getAmount() / 2, curr.getDataComponentsPatch());
            } else if (Utils.isEmpty(curr) && !Utils.isEmpty(to)) {
                container.put(slot, new ItemStack(to.getId(), 1, to.getDataComponentsPatch()));
                return to.getAmount() == 1 ? new ItemStack(0) : new ItemStack(to.getId(), to.getAmount() - 1, to.getDataComponentsPatch());
            } else if (curr != null && to.getDataComponentsPatch() == curr.getDataComponentsPatch()) {
                container.put(slot, new ItemStack(curr.getId(), curr.getAmount() + amount, curr.getDataComponentsPatch()));
                return amount == to.getAmount() ? new ItemStack(0) : new ItemStack(to.getId(), to.getAmount() - amount, to.getDataComponentsPatch());
            }
        }
        return to;
    }

    public static boolean mayPlace(ItemStack to, ItemStack curr) {
        return to == curr || to != null && curr != null && to.getId() == curr.getId();
    }

    public Container copy() {
        return new Container(this.windowId, this.type, this.windowSize, this.container.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Utils.copyStack(e.getValue()))), this.title);
    }

}
