package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundRecipeBookRemovePacket extends Packet {
    private int[] recipes;

    public ClientboundRecipeBookRemovePacket(ByteBuf data) {
        super(data);
    }

    public ClientboundRecipeBookRemovePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        int recipeCount = MinecraftTypes.readVarInt(in);
        int[] recipes = new int[recipeCount];
        for (int index = 0; index < recipeCount; index++) {
            recipes[index] = MinecraftTypes.readVarInt(in);
        }
        this.recipes = recipes;
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.recipes.length);
        for (int recipe : recipes) {
            MinecraftTypes.writeVarInt(out, recipe);
        }
    }
}
