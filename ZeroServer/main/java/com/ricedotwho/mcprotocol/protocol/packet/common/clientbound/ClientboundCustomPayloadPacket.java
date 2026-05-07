package com.ricedotwho.mcprotocol.protocol.packet.common.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundCustomPayloadPacket extends Packet {
    private @NonNull Key channel;
    private byte @NonNull [] data;

    public ClientboundCustomPayloadPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundCustomPayloadPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.channel = MinecraftTypes.readResourceLocation(in);
        this.data = MinecraftTypes.readByteArray(in, ByteBuf::readableBytes);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeResourceLocation(out, this.channel);
        out.writeBytes(this.data);
    }
}
