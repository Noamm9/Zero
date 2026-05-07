package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity.player;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudburstmc.math.vector.Vector3d;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.PositionElement;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundPlayerPositionPacket extends Packet {
    private int entityId;
    private Vector3d position;
    private Vector3d deltaMovement;
    private float yRot;
    private float xRot;
    private @NonNull List<PositionElement> relatives;

    public ClientboundPlayerPositionPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundPlayerPositionPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.entityId = MinecraftTypes.readVarInt(in);
        this.position = Vector3d.from(in.readDouble(), in.readDouble(), in.readDouble());
        this.deltaMovement = Vector3d.from(in.readDouble(), in.readDouble(), in.readDouble());
        this.yRot = in.readFloat();
        this.xRot = in.readFloat();

        this.relatives = new ArrayList<>();
        int flags = in.readInt();
        for (PositionElement element : PositionElement.values()) {
            int bit = 1 << element.ordinal();
            if ((flags & bit) == bit) {
                this.relatives.add(element);
            }
        }
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.entityId);
        out.writeDouble(this.position.getX());
        out.writeDouble(this.position.getY());
        out.writeDouble(this.position.getZ());
        out.writeDouble(this.deltaMovement.getX());
        out.writeDouble(this.deltaMovement.getY());
        out.writeDouble(this.deltaMovement.getZ());
        out.writeFloat(this.yRot);
        out.writeFloat(this.xRot);

        int flags = 0;
        for (PositionElement element : this.relatives) {
            flags |= 1 << element.ordinal();
        }

        out.writeInt(flags);
    }
}
