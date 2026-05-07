package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.math.vector.Vector3i;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
public class ClientboundGameTestHighlightPosPacket extends Packet {
    private Vector3i absolutePos;
    private Vector3i relativePos;

    public ClientboundGameTestHighlightPosPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundGameTestHighlightPosPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.absolutePos = MinecraftTypes.readPosition(in);
        this.relativePos = MinecraftTypes.readPosition(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writePosition(out, this.absolutePos);
        MinecraftTypes.writePosition(out, this.relativePos);
    }
}
