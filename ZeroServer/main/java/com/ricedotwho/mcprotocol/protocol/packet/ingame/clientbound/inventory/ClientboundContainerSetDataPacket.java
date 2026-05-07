package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.inventory;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundContainerSetDataPacket extends Packet {
    private int containerId;
    private int rawProperty;
    private int value;

    public ClientboundContainerSetDataPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundContainerSetDataPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.containerId = MinecraftTypes.readVarInt(in);
        this.rawProperty = in.readShort();
        this.value = in.readShort();
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.containerId);
        out.writeShort(this.rawProperty);
        out.writeShort(this.value);
    }
}
