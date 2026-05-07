package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundSetSimulationDistancePacket extends Packet {
    private int simulationDistance;

    public ClientboundSetSimulationDistancePacket(ByteBuf data) {
        super(data);
    }

    public ClientboundSetSimulationDistancePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.simulationDistance = MinecraftTypes.readVarInt(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.simulationDistance);
    }
}
