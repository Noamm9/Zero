package com.ricedotwho.mcprotocol.protocol.packet.common.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundUpdateTagsPacket extends Packet {
    private final @NonNull Map<Key, Map<Key, int[]>> tags = new HashMap<>();

    public ClientboundUpdateTagsPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundUpdateTagsPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        int totalTagCount = MinecraftTypes.readVarInt(in);
        for (int i = 0; i < totalTagCount; i++) {
            Map<Key, int[]> tag = new HashMap<>();
            Key tagName = MinecraftTypes.readResourceLocation(in);
            int tagsCount = MinecraftTypes.readVarInt(in);
            for (int j = 0; j < tagsCount; j++) {
                Key name = MinecraftTypes.readResourceLocation(in);
                int entriesCount = MinecraftTypes.readVarInt(in);
                int[] entries = new int[entriesCount];
                for (int index = 0; index < entriesCount; index++) {
                    entries[index] = MinecraftTypes.readVarInt(in);
                }

                tag.put(name, entries);
            }
            tags.put(tagName, tag);
        }
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, tags.size());
        for (Map.Entry<Key, Map<Key, int[]>> tagSet : tags.entrySet()) {
            MinecraftTypes.writeResourceLocation(out, tagSet.getKey());
            MinecraftTypes.writeVarInt(out, tagSet.getValue().size());
            for (Map.Entry<Key, int[]> tag : tagSet.getValue().entrySet()) {
                MinecraftTypes.writeResourceLocation(out, tag.getKey());
                MinecraftTypes.writeVarInt(out, tag.getValue().length);
                for (int id : tag.getValue()) {
                    MinecraftTypes.writeVarInt(out, id);
                }
            }
        }
    }
}
