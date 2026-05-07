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
public class ServerboundContainerSlotStateChangedPacket extends Packet {
    private int slotId;
    private int containerId;
    private boolean newState;

    public ServerboundContainerSlotStateChangedPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundContainerSlotStateChangedPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.slotId = MinecraftTypes.readVarInt(in);
        this.containerId = MinecraftTypes.readVarInt(in);
        this.newState = in.readBoolean();
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.slotId);
        MinecraftTypes.writeVarInt(out, this.containerId);
        out.writeBoolean(this.newState);
    }
}
