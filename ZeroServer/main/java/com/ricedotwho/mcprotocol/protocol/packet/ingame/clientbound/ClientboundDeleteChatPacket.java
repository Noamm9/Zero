package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.chat.MessageSignature;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundDeleteChatPacket extends Packet {
    private MessageSignature messageSignature;

    public ClientboundDeleteChatPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundDeleteChatPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.messageSignature = MessageSignature.read(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.messageSignature.getId() + 1);
        if (this.messageSignature.getMessageSignature() != null) {
            out.writeBytes(messageSignature.getMessageSignature());
        }
    }
}
