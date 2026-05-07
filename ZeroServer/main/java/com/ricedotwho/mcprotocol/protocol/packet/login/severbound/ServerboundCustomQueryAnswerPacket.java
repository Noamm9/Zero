package com.ricedotwho.mcprotocol.protocol.packet.login.severbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundCustomQueryAnswerPacket extends Packet {
    private int transactionId;
    private byte @Nullable [] data;

    public ServerboundCustomQueryAnswerPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundCustomQueryAnswerPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.transactionId = MinecraftTypes.readVarInt(in);
        this.data = MinecraftTypes.readNullable(in, buf -> MinecraftTypes.readByteArray(buf, ByteBuf::readableBytes));
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.transactionId);
        MinecraftTypes.writeNullable(out, this.data, ByteBuf::writeBytes);
    }
}
