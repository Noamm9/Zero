package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.recipe.Ingredient;
import org.geysermc.mcprotocollib.protocol.data.game.recipe.display.slot.SlotDisplay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundUpdateRecipesPacket extends Packet {
    private Map<Key, int[]> itemSets;
    private List<SelectableRecipe> stonecutterRecipes;

    public ClientboundUpdateRecipesPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundUpdateRecipesPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.itemSets = new HashMap<>();
        int itemCount = MinecraftTypes.readVarInt(in);
        for (int i = 0; i < itemCount; i++) {
            Key propertySetType = MinecraftTypes.readResourceLocation(in);
            int[] propertySet = new int[MinecraftTypes.readVarInt(in)];
            for (int j = 0; j < propertySet.length; j++) {
                propertySet[j] = MinecraftTypes.readVarInt(in);
            }
            this.itemSets.put(propertySetType, propertySet);
        }

        this.stonecutterRecipes = MinecraftTypes.readList(in, buf -> {
            Ingredient input = MinecraftTypes.readRecipeIngredient(buf);
            SlotDisplay recipe = MinecraftTypes.readSlotDisplay(buf);
            return new SelectableRecipe(input, recipe);
        });
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.itemSets.size());
        for (Map.Entry<Key, int[]> itemSet : this.itemSets.entrySet()) {
            MinecraftTypes.writeResourceLocation(out, itemSet.getKey());

            MinecraftTypes.writeVarInt(out, itemSet.getValue().length);
            for (int property : itemSet.getValue()) {
                MinecraftTypes.writeVarInt(out, property);
            }
        }

        MinecraftTypes.writeList(out, this.stonecutterRecipes, (buf, recipes) -> {
            MinecraftTypes.writeRecipeIngredient(buf, recipes.input());
            MinecraftTypes.writeSlotDisplay(buf, recipes.recipe());
        });
    }

    public record SelectableRecipe(Ingredient input, SlotDisplay recipe) {
    }
}
