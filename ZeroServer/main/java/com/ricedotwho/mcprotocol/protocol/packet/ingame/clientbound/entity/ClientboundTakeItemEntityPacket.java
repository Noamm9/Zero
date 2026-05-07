package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundTakeItemEntityPacket extends Packet {
    private int collectedEntityId;
    private int collectorEntityId;
    private int itemCount;

    public ClientboundTakeItemEntityPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundTakeItemEntityPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.collectedEntityId = MinecraftTypes.readVarInt(in);
        this.collectorEntityId = MinecraftTypes.readVarInt(in);
        this.itemCount = MinecraftTypes.readVarInt(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.collectedEntityId);
        MinecraftTypes.writeVarInt(out, this.collectorEntityId);
        MinecraftTypes.writeVarInt(out, this.itemCount);
    }
}
