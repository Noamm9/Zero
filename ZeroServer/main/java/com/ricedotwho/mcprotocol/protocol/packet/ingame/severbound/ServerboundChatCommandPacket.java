package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundChatCommandPacket extends Packet {
    private String command;

    public ServerboundChatCommandPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundChatCommandPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.command = MinecraftTypes.readString(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeString(out, this.command);
    }
}
