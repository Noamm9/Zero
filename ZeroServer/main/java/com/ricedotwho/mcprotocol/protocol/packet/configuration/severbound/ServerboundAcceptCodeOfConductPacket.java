package com.ricedotwho.mcprotocol.protocol.packet.configuration.severbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundAcceptCodeOfConductPacket extends Packet {
    public static final ServerboundAcceptCodeOfConductPacket INSTANCE = new ServerboundAcceptCodeOfConductPacket();

    public ServerboundAcceptCodeOfConductPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundAcceptCodeOfConductPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {

    }

    @Override
    public void encode(ByteBuf out) {

    }
}
