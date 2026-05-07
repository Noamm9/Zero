package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity.player;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundBlockChangedAckPacket extends Packet {
    private int sequence;

    public ClientboundBlockChangedAckPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundBlockChangedAckPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.sequence = MinecraftTypes.readVarInt(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.sequence);
    }
}
