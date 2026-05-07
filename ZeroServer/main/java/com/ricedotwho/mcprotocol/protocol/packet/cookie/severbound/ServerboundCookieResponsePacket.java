package com.ricedotwho.mcprotocol.protocol.packet.cookie.severbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundCookieResponsePacket extends Packet {
    private Key key;
    private byte @Nullable [] payload;

    public ServerboundCookieResponsePacket(ByteBuf data) {
        super(data);
    }

    public ServerboundCookieResponsePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.key = MinecraftTypes.readResourceLocation(in);
        this.payload = MinecraftTypes.readNullable(in, MinecraftTypes::readByteArray);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeResourceLocation(out, this.key);
        MinecraftTypes.writeNullable(out, this.payload, MinecraftTypes::writeByteArray);
    }
}
