package com.ricedotwho.mcprotocol.protocol.packet.configuration.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.KnownPack;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundSelectKnownPacksPacket extends Packet {
    private List<KnownPack> knownPacks;

    public ClientboundSelectKnownPacksPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundSelectKnownPacksPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.knownPacks = MinecraftTypes.readList(in, buf -> new KnownPack(MinecraftTypes.readString(buf), MinecraftTypes.readString(buf), MinecraftTypes.readString(buf)));
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeList(out, this.knownPacks, (buf, entry) -> {
            MinecraftTypes.writeString(buf, entry.getNamespace());
            MinecraftTypes.writeString(buf, entry.getId());
            MinecraftTypes.writeString(buf, entry.getVersion());
        });
    }
}
