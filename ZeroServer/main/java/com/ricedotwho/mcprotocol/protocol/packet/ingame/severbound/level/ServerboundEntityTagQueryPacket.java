package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.level;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundEntityTagQueryPacket extends Packet {
    private int transactionId;
    private int entityId;

    public ServerboundEntityTagQueryPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundEntityTagQueryPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.transactionId = MinecraftTypes.readVarInt(in);
        this.entityId = MinecraftTypes.readVarInt(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.transactionId);
        MinecraftTypes.writeVarInt(out, this.entityId);
    }
}
