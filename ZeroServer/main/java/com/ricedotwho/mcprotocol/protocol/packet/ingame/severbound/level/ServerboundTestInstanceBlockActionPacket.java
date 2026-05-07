package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.level;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.math.vector.Vector3i;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.level.block.TestInstanceBlockEntity;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundTestInstanceBlockActionPacket extends Packet {
    private Vector3i pos;
    private int action;
    private TestInstanceBlockEntity data;

    public ServerboundTestInstanceBlockActionPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundTestInstanceBlockActionPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.pos = MinecraftTypes.readPosition(in);
        this.action = MinecraftTypes.readVarInt(in);
        this.data = MinecraftTypes.readTestBlockEntity(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writePosition(out, this.pos);
        MinecraftTypes.writeVarInt(out, this.action);
        MinecraftTypes.writeTestBlockEntity(out, this.data);
    }
}
