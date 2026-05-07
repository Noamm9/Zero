package com.ricedotwho.mcprotocol.protocol.packet.configuration.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundCodeOfConductPacket extends Packet {
    private String codeOfConduct;

    public ClientboundCodeOfConductPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundCodeOfConductPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.codeOfConduct = MinecraftTypes.readString(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeString(out, codeOfConduct);
    }
}
