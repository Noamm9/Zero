package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.player;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudburstmc.math.vector.Vector3i;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.entity.object.Direction;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.PlayerAction;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundPlayerActionPacket extends Packet {
    private @NonNull PlayerAction action;
    private @NonNull Vector3i position;
    private @NonNull Direction face;
    private int sequence;

    public ServerboundPlayerActionPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundPlayerActionPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.action = PlayerAction.from(MinecraftTypes.readVarInt(in));
        this.position = MinecraftTypes.readPosition(in);
        this.face = Direction.from(in.readUnsignedByte());
        this.sequence = MinecraftTypes.readVarInt(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.action.ordinal());
        MinecraftTypes.writePosition(out, this.position);
        out.writeByte(this.face.ordinal());
        MinecraftTypes.writeVarInt(out, this.sequence);
    }
}
