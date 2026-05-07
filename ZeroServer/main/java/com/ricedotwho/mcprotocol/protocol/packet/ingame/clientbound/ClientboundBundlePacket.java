package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundBundlePacket extends Packet {
    private List<Packet> packets;

    public ClientboundBundlePacket(ByteBuf data) {
        super(data);
    }

    public ClientboundBundlePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {

    }

    public void decode(List<Packet> packets) {
        this.packets = packets;
    }

    @Override
    public void encode(ByteBuf out) {

    }
}
