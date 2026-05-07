package com.ricedotwho.zero.util;

import com.ricedotwho.zero.Zero;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.geysermc.mcprotocollib.protocol.data.game.inventory.ContainerType;
import org.geysermc.mcprotocollib.protocol.data.game.item.HashedStack;
import org.geysermc.mcprotocollib.protocol.data.game.item.ItemStack;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.*;

import java.util.*;

@UtilityClass
public class Utils {

    public double calcDist(double x1, double y1, double z1, double x2, double y2, double z2) {
        double x = x2 - x1;
        double y = y2 - y1;
        double z = z2 - z1;

        double ret = x * x + y * y + z * z;

        if (ret < 0) ret *= -1;
        return ret;
    }

    public double calcDist(Pos pos1, Pos pos2) {
        return calcDist(pos1.x(), pos1.y(), pos1.z(), pos2.x(), pos2.y(), pos2.z());
    }

    public boolean equalsOneOf(Object object, Object... others) {
        for (Object obj : others) {
            if (Objects.equals(object, obj)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInteger(String str) {
        return str.matches("-?\\d+");
    }

    public boolean isInside(Pos pos, Pos min, Pos max) {
        return max.x() >= pos.x() && min.x() <= pos.x()
                && max.y() >= pos.y() && min.y() <= pos.y()
                && max.z() >= pos.z() && min.z() <= pos.z();
    }

    public boolean isEmpty(ItemStack itemStack) {
        return itemStack == null || itemStack.getId() == 0 || itemStack.getAmount() <= 0;
    }

    public int getGuiSlotCount(ContainerType menuType) {
        if (menuType == ContainerType.GENERIC_9X4) return 36;
        if (menuType == ContainerType.GENERIC_9X5) return 45;
        if (menuType == ContainerType.GENERIC_9X6) return 54;
        return -1;
    }

    public HashedStack toHashedStack(ItemStack stack) {
        if (isEmpty(stack)) {
            return null;
        }

        Map<DataComponentType<?>, Integer> added = new HashMap<>();

        if (stack.getDataComponentsPatch() != null) {
            stack.getDataComponentsPatch().getDataComponents().forEach((type, value) -> {
                if (value != null) {
                    added.put(type, value.hashCode());
                }
            });
        }

        return new HashedStack(
                stack.getId(),
                stack.getAmount(),
                added,
                new HashSet<>()
        );
    }

    public boolean isEnchanted(ItemStack item) {
        if (item == null || item.getDataComponentsPatch() == null) return false;
        return item.getDataComponentsPatch().getOrDefault(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false) || item.getDataComponentsPatch().getOrDefault(DataComponentTypes.ENCHANTMENTS, null) != null;
    }

    public boolean isEnchantable(ItemStack item) {
        if (item == null || item.getDataComponentsPatch() == null) return false;
        return item.getDataComponentsPatch().get(DataComponentTypes.ENCHANTABLE) != null;
    }

    public Component getCustomName(ItemStack item) {
        if (item == null || item.getDataComponentsPatch() == null) return Component.empty();
        return item.getDataComponentsPatch().getOrDefault(DataComponentTypes.CUSTOM_NAME, Component.empty());
    }

    public boolean itemsEqual(ItemStack a, ItemStack b) {
        if (a == b) {
            return true;
        } else if (a.getId() != b.getId() || a.getAmount() != b.getAmount()) {
            return false;
        } else {
            return componentsEqualIgnoringCustomData(a.getDataComponentsPatch(), b.getDataComponentsPatch());
        }
    }

    public boolean componentsEqualIgnoringCustomData(DataComponents a, DataComponents b) {
        if (Objects.equals(a, b)) return true;
        Map<DataComponentType<?>, DataComponent<?, ?>> aC = a.getDataComponents();
        Map<DataComponentType<?>, DataComponent<?, ?>> bC = b.getDataComponents();

        for (Map.Entry<DataComponentType<?>, DataComponent<?, ?>> e : a.getDataComponents().entrySet()) {
            if (e.getKey().equals(DataComponentTypes.CUSTOM_DATA)) continue;
            if (!e.getValue().equals(bC.get(e.getKey()))) {
                Zero.getLogger().info("{} != {}", e.getValue(), bC.get(e.getKey()));
                return false;
            }
        }

        for (Map.Entry<DataComponentType<?>, DataComponent<?, ?>> e : b.getDataComponents().entrySet()) {
            if (e.getKey().equals(DataComponentTypes.CUSTOM_DATA)) continue;
            if (!e.getValue().equals(aC.get(e.getKey()))) {
                Zero.getLogger().info("{} != {}", e.getValue(), aC.get(e.getKey()));
                return false;
            }
        }

        return true;
    }

    public Component getBlank() {
        return Component.empty().decoration(TextDecoration.ITALIC, false);
    }

    public ItemStack copyStack(ItemStack other) {
        return new ItemStack(other.getId(), other.getAmount(), other.getDataComponentsPatch());
    }
}
