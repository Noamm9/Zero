package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.level;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudburstmc.math.vector.Vector3i;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundBlockEntityTagQueryPacket extends Packet {
    private int transactionId;
    private @NonNull Vector3i position;

    public ServerboundBlockEntityTagQueryPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundBlockEntityTagQueryPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.transactionId = MinecraftTypes.readVarInt(in);
        this.position = MinecraftTypes.readPosition(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.transactionId);
        MinecraftTypes.writePosition(out, this.position);
    }
}
