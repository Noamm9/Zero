package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.Holder;
import org.geysermc.mcprotocollib.protocol.data.game.chat.ChatType;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundDisguisedChatPacket extends Packet {
    private Component message;
    private Holder<ChatType> chatType;
    private Component name;
    private @Nullable Component targetName;

    public ClientboundDisguisedChatPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundDisguisedChatPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.message = MinecraftTypes.readComponent(in);
        this.chatType = MinecraftTypes.readHolder(in, MinecraftTypes::readChatType);
        this.name = MinecraftTypes.readComponent(in);
        this.targetName = MinecraftTypes.readNullable(in, MinecraftTypes::readComponent);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeComponent(out, this.message);
        MinecraftTypes.writeHolder(out, this.chatType, MinecraftTypes::writeChatType);
        MinecraftTypes.writeComponent(out, this.name);
        MinecraftTypes.writeNullable(out, this.targetName, MinecraftTypes::writeComponent);
    }
}
