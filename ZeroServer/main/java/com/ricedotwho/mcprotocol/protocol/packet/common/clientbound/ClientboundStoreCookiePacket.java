package com.ricedotwho.mcprotocol.protocol.packet.common.clientbound;

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
public class ClientboundStoreCookiePacket extends Packet {
    private Key key;
    private byte[] payload;

    public ClientboundStoreCookiePacket(ByteBuf data) {
        super(data);
    }

    public ClientboundStoreCookiePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.key = MinecraftTypes.readResourceLocation(in);
        this.payload = MinecraftTypes.readByteArray(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeResourceLocation(out, this.key);
        MinecraftTypes.writeByteArray(out, this.payload);
    }
}
