package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.inventory;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundPlaceRecipePacket extends Packet {
    private int containerId;
    private int recipe;
    private boolean useMaxItems;

    public ServerboundPlaceRecipePacket(ByteBuf data) {
        super(data);
    }

    public ServerboundPlaceRecipePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.containerId = MinecraftTypes.readVarInt(in);
        this.recipe = MinecraftTypes.readVarInt(in);
        this.useMaxItems = in.readBoolean();
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.containerId);
        MinecraftTypes.writeVarInt(out, this.recipe);
        out.writeBoolean(this.useMaxItems);
    }
}
