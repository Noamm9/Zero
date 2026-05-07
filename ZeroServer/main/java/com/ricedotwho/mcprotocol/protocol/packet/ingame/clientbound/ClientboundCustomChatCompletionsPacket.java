package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.chat.ChatCompletionAction;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundCustomChatCompletionsPacket extends Packet {
    private ChatCompletionAction action;
    private String[] entries;

    public ClientboundCustomChatCompletionsPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundCustomChatCompletionsPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.action = ChatCompletionAction.from(MinecraftTypes.readVarInt(in));
        this.entries = new String[MinecraftTypes.readVarInt(in)];
        for (int i = 0; i < this.entries.length; i++) {
            this.entries[i] = MinecraftTypes.readString(in);
        }
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.action.ordinal());
        MinecraftTypes.writeVarInt(out, this.entries.length);
        for (String entry : this.entries) {
            MinecraftTypes.writeString(out, entry);
        }
    }
}
