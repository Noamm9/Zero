package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundCommandSuggestionPacket extends Packet {
    private int transactionId;
    private @NonNull String text;

    public ServerboundCommandSuggestionPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundCommandSuggestionPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.transactionId = MinecraftTypes.readVarInt(in);
        this.text = MinecraftTypes.readString(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.transactionId);
        MinecraftTypes.writeString(out, this.text);
    }
}
