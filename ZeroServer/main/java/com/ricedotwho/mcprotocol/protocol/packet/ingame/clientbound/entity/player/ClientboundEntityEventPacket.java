package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity.player;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudburstmc.math.vector.Vector3d;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.entity.EntityEvent;
import org.geysermc.mcprotocollib.protocol.data.game.entity.object.*;
import org.geysermc.mcprotocollib.protocol.data.game.entity.type.EntityType;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundEntityEventPacket extends Packet {
    private int entityId;
    private @NonNull EntityEvent event;

    public ClientboundEntityEventPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundEntityEventPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.entityId = in.readInt();
        this.event = MinecraftTypes.readEntityEvent(in);
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeInt(this.entityId);
        MinecraftTypes.writeEntityEvent(out, this.event);
    }
}
