package com.ricedotwho.mcprotocol.protocol.packet.handshake.serverbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.handshake.HandshakeIntent;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundClientIntentionPacket extends Packet {
    private int protocolVersion;
    private @NonNull String hostname;
    private int port;
    private @NonNull HandshakeIntent intent;

    public ServerboundClientIntentionPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundClientIntentionPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.protocolVersion = MinecraftTypes.readVarInt(in);
        this.hostname = MinecraftTypes.readString(in);
        this.port = in.readUnsignedShort();
        this.intent = HandshakeIntent.from(MinecraftTypes.readVarInt(in) - 1);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.protocolVersion);
        MinecraftTypes.writeString(out, this.hostname);
        out.writeShort(this.port);
        MinecraftTypes.writeVarInt(out, this.intent.ordinal() + 1);
    }
}
