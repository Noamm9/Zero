package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundRecipeBookSettingsPacket extends Packet {
    private TypeSettings crafting;
    private TypeSettings furnace;
    private TypeSettings blastFurnace;
    private TypeSettings smoker;

    public ClientboundRecipeBookSettingsPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundRecipeBookSettingsPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.crafting = new TypeSettings(in.readBoolean(), in.readBoolean());
        this.furnace = new TypeSettings(in.readBoolean(), in.readBoolean());
        this.blastFurnace = new TypeSettings(in.readBoolean(), in.readBoolean());
        this.smoker = new TypeSettings(in.readBoolean(), in.readBoolean());
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeBoolean(this.crafting.open());
        out.writeBoolean(this.crafting.filtering());
        out.writeBoolean(this.furnace.open());
        out.writeBoolean(this.furnace.filtering());
        out.writeBoolean(this.blastFurnace.open());
        out.writeBoolean(this.blastFurnace.filtering());
        out.writeBoolean(this.smoker.open());
        out.writeBoolean(this.smoker.filtering());
    }

    public record TypeSettings(boolean open, boolean filtering) {
    }
}
