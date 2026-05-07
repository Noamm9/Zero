package com.ricedotwho.mcprotocol.protocol.packet.configuration.severbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.KnownPack;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundSelectKnownPacksPacket extends Packet {
    private List<KnownPack> knownPacks;

    public ServerboundSelectKnownPacksPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundSelectKnownPacksPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.knownPacks = new ArrayList<>();

        int entryCount = Math.min(MinecraftTypes.readVarInt(in), 64);
        for (int i = 0; i < entryCount; i++) {
            this.knownPacks.add(new KnownPack(MinecraftTypes.readString(in), MinecraftTypes.readString(in), MinecraftTypes.readString(in)));
        }
    }

    @Override
    public void encode(ByteBuf out) {
        if (this.knownPacks.size() > 64) {
            throw new IllegalArgumentException("KnownPacks is longer than maximum allowed length");
        }

        MinecraftTypes.writeVarInt(out, this.knownPacks.size());
        for (KnownPack entry : this.knownPacks) {
            MinecraftTypes.writeString(out, entry.getNamespace());
            MinecraftTypes.writeString(out, entry.getId());
            MinecraftTypes.writeString(out, entry.getVersion());
        }
    }
}
