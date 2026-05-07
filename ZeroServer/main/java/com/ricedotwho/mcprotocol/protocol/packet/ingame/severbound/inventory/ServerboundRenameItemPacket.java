package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.inventory;

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
public class ServerboundRenameItemPacket extends Packet {
    private @NonNull String name;

    public ServerboundRenameItemPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundRenameItemPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.name = MinecraftTypes.readString(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeString(out, this.name);
    }
}
