package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.level;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.math.vector.Vector3i;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundSetTestBlockPacket extends Packet {
    private Vector3i position;
    private int mode;
    private String message;

    public ServerboundSetTestBlockPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundSetTestBlockPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.position = MinecraftTypes.readPosition(in);
        this.mode = MinecraftTypes.readVarInt(in);
        this.message = MinecraftTypes.readString(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writePosition(out, this.position);
        MinecraftTypes.writeVarInt(out, this.mode);
        MinecraftTypes.writeString(out, this.message);
    }
}
