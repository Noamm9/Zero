package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.inventory;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.jspecify.annotations.Nullable;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundEditBookPacket extends Packet {
    private int slot;
    private List<String> pages;
    private @Nullable String title;

    public ServerboundEditBookPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundEditBookPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.slot = MinecraftTypes.readVarInt(in);
        this.pages = MinecraftTypes.readList(in, MinecraftTypes::readString);
        this.title = MinecraftTypes.readNullable(in, MinecraftTypes::readString);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, slot);
        MinecraftTypes.writeList(out, pages, MinecraftTypes::writeString);
        MinecraftTypes.writeNullable(out, title, MinecraftTypes::writeString);
    }
}
