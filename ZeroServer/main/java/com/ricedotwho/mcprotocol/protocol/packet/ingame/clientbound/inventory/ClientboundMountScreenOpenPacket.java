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
public class ClientboundMountScreenOpenPacket extends Packet {
    private int containerId;
    private int inventoryColumns;
    private int entityId;

    public ClientboundMountScreenOpenPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundMountScreenOpenPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.containerId = MinecraftTypes.readVarInt(in);
        this.inventoryColumns = MinecraftTypes.readVarInt(in);
        this.entityId = in.readInt();
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.containerId);
        MinecraftTypes.writeVarInt(out, this.inventoryColumns);
        out.writeInt(this.entityId);
    }
}
