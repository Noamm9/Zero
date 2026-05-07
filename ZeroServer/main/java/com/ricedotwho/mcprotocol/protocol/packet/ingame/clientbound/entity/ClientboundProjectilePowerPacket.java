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
public class ClientboundProjectilePowerPacket extends Packet {
    private int projId;
    private double accelerationPower;

    public ClientboundProjectilePowerPacket(ByteBuf data) {
        super( data);
    }

    public ClientboundProjectilePowerPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.projId = MinecraftTypes.readVarInt(in);
        this.accelerationPower = in.readDouble();
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.projId);
        out.writeDouble(this.accelerationPower);
    }
}
