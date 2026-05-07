package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.level;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudburstmc.math.vector.Vector3i;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundJigsawGeneratePacket extends Packet {
    private @NonNull Vector3i position;
    private int levels;
    private boolean keepJigsaws;

    public ServerboundJigsawGeneratePacket(ByteBuf data) {
        super(data);
    }

    public ServerboundJigsawGeneratePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.position = MinecraftTypes.readPosition(in);
        this.levels = MinecraftTypes.readVarInt(in);
        this.keepJigsaws = in.readBoolean();
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writePosition(out, this.position);
        MinecraftTypes.writeVarInt(out, this.levels);
        out.writeBoolean(this.keepJigsaws);
    }
}
