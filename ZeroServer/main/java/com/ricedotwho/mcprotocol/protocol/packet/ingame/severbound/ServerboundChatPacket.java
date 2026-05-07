package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.jspecify.annotations.Nullable;

import java.util.BitSet;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundChatPacket extends Packet {
    private @NonNull String message;
    private long timeStamp;
    private long salt;
    private byte @Nullable [] signature;
    private int offset;
    private BitSet acknowledgedMessages;
    private int checksum;

    public ServerboundChatPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundChatPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.message = MinecraftTypes.readString(in);
        this.timeStamp = in.readLong();
        this.salt = in.readLong();
        this.signature = MinecraftTypes.readNullable(in, buf -> {
            byte[] signature = new byte[256];
            buf.readBytes(signature);
            return signature;
        });

        this.offset = MinecraftTypes.readVarInt(in);
        this.acknowledgedMessages = MinecraftTypes.readFixedBitSet(in, 20);
        this.checksum = in.readByte();
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeString(out, this.message);
        out.writeLong(this.timeStamp);
        out.writeLong(this.salt);
        MinecraftTypes.writeNullable(out, this.signature, ByteBuf::writeBytes);

        MinecraftTypes.writeVarInt(out, this.offset);
        MinecraftTypes.writeFixedBitSet(out, this.acknowledgedMessages, 20);
        out.writeByte(this.checksum);
    }
}
