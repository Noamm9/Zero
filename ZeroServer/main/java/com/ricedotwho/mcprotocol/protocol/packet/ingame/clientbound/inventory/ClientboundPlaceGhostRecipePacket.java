package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.inventory;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.recipe.display.RecipeDisplay;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundPlaceGhostRecipePacket extends Packet {
    private int containerId;
    private RecipeDisplay recipeDisplay;

    public ClientboundPlaceGhostRecipePacket(ByteBuf data) {
        super(data);
    }

    public ClientboundPlaceGhostRecipePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.containerId = MinecraftTypes.readVarInt(in);
        this.recipeDisplay = MinecraftTypes.readRecipeDisplay(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.containerId);
        MinecraftTypes.writeRecipeDisplay(out, this.recipeDisplay);
    }
}
