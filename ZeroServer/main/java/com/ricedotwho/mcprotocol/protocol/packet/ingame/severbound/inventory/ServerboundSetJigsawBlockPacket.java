package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.inventory;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import org.cloudburstmc.math.vector.Vector3i;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundSetJigsawBlockPacket extends Packet {
    private @NonNull Vector3i position;
    private @NonNull Key name;
    private @NonNull Key target;
    private @NonNull Key pool;
    private @NonNull String finalState;
    private @NonNull String jointType;
    private int selectionPriority;
    private int placementPriority;

    public ServerboundSetJigsawBlockPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundSetJigsawBlockPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.position = MinecraftTypes.readPosition(in);
        this.name = MinecraftTypes.readResourceLocation(in);
        this.target = MinecraftTypes.readResourceLocation(in);
        this.pool = MinecraftTypes.readResourceLocation(in);
        this.finalState = MinecraftTypes.readString(in);
        this.jointType = MinecraftTypes.readString(in);
        this.selectionPriority = MinecraftTypes.readVarInt(in);
        this.placementPriority = MinecraftTypes.readVarInt(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writePosition(out, this.position);
        MinecraftTypes.writeResourceLocation(out, this.name);
        MinecraftTypes.writeResourceLocation(out, this.target);
        MinecraftTypes.writeResourceLocation(out, this.pool);
        MinecraftTypes.writeString(out, this.finalState);
        MinecraftTypes.writeString(out, this.jointType);
        MinecraftTypes.writeVarInt(out, this.selectionPriority);
        MinecraftTypes.writeVarInt(out, this.placementPriority);
    }
}
