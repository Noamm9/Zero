package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.inventory;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.math.vector.Vector3i;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundPickItemFromBlockPacket extends Packet {
    private Vector3i pos;
    private boolean includeData;

    public ServerboundPickItemFromBlockPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundPickItemFromBlockPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.pos = MinecraftTypes.readPosition(in);
        this.includeData = in.readBoolean();
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writePosition(out, this.pos);
        out.writeBoolean(this.includeData);
    }
}
