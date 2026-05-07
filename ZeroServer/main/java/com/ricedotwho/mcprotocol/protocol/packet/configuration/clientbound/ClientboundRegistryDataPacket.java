package com.ricedotwho.mcprotocol.protocol.packet.configuration.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.RegistryEntry;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundRegistryDataPacket extends Packet {
    private Key registry;
    private List<RegistryEntry> entries;

    public ClientboundRegistryDataPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundRegistryDataPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.registry = MinecraftTypes.readResourceLocation(in);
        this.entries = MinecraftTypes.readList(in, buf -> new RegistryEntry(MinecraftTypes.readResourceLocation(buf), MinecraftTypes.readNullable(buf, MinecraftTypes::readCompoundTag)));
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeResourceLocation(out, this.registry);
        MinecraftTypes.writeList(out, this.entries, (buf, entry) -> {
            MinecraftTypes.writeResourceLocation(buf, entry.getId());
            MinecraftTypes.writeNullable(buf, entry.getData(), MinecraftTypes::writeAnyTag);
        });
    }
}
