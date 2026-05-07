package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.Holder;
import org.geysermc.mcprotocollib.protocol.data.game.chat.ChatFilterType;
import org.geysermc.mcprotocollib.protocol.data.game.chat.ChatType;
import org.geysermc.mcprotocollib.protocol.data.game.chat.MessageSignature;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundPlayerChatPacket extends Packet {
    private int globalIndex;
    private UUID sender;
    private int index;
    private byte @Nullable [] messageSignature;
    private String content;
    private long timeStamp;
    private long salt;
    private List<MessageSignature> lastSeenMessages;
    private @Nullable Component unsignedContent;
    private ChatFilterType filterMask;
    private Holder<ChatType> chatType;
    private Component name;
    private @Nullable Component targetName;

    public ClientboundPlayerChatPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundPlayerChatPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.globalIndex = MinecraftTypes.readVarInt(in);
        this.sender = MinecraftTypes.readUUID(in);
        this.index = MinecraftTypes.readVarInt(in);
        this.messageSignature = MinecraftTypes.readNullable(in, buf -> {
            byte[] signature = new byte[256];
            buf.readBytes(signature);
            return signature;
        });

        this.content = MinecraftTypes.readString(in, 256);
        this.timeStamp = in.readLong();
        this.salt = in.readLong();

        this.lastSeenMessages = new ArrayList<>();
        int seenMessageCount = Math.min(MinecraftTypes.readVarInt(in), 20);
        for (int i = 0; i < seenMessageCount; i++) {
            this.lastSeenMessages.add(MessageSignature.read(in));
        }

        this.unsignedContent = MinecraftTypes.readNullable(in, MinecraftTypes::readComponent);
        this.filterMask = ChatFilterType.from(MinecraftTypes.readVarInt(in));
        this.chatType = MinecraftTypes.readHolder(in, MinecraftTypes::readChatType);
        this.name = MinecraftTypes.readComponent(in);
        this.targetName = MinecraftTypes.readNullable(in, MinecraftTypes::readComponent);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.globalIndex);
        MinecraftTypes.writeUUID(out, this.sender);
        MinecraftTypes.writeVarInt(out, this.index);
        MinecraftTypes.writeNullable(out, this.messageSignature, ByteBuf::writeBytes);

        MinecraftTypes.writeString(out, this.content);
        out.writeLong(this.timeStamp);
        out.writeLong(this.salt);

        MinecraftTypes.writeVarInt(out, this.lastSeenMessages.size());
        for (MessageSignature messageSignature : this.lastSeenMessages) {
            MinecraftTypes.writeVarInt(out, messageSignature.getId() + 1);
            if (messageSignature.getMessageSignature() != null) {
                out.writeBytes(messageSignature.getMessageSignature());
            }
        }

        MinecraftTypes.writeNullable(out, this.unsignedContent, MinecraftTypes::writeComponent);
        MinecraftTypes.writeVarInt(out, this.filterMask.ordinal());
        MinecraftTypes.writeHolder(out, this.chatType, MinecraftTypes::writeChatType);
        MinecraftTypes.writeComponent(out, this.name);
        MinecraftTypes.writeNullable(out, this.targetName, MinecraftTypes::writeComponent);
    }
}
