package com.ricedotwho.mcprotocol.protocol.packet.login.severbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundPlayerLoadedPacket extends Packet {
    public static final ServerboundPlayerLoadedPacket INSTANCE = new ServerboundPlayerLoadedPacket();

    public ServerboundPlayerLoadedPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundPlayerLoadedPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {

    }

    @Override
    public void encode(ByteBuf out) {

    }
}
