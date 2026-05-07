package com.ricedotwho.mcprotocol.protocol.packet.cookie.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundCookieRequestPacket extends Packet {
    private Key key;

    public ClientboundCookieRequestPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundCookieRequestPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.key = MinecraftTypes.readResourceLocation(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeResourceLocation(out, this.key);
    }
}
