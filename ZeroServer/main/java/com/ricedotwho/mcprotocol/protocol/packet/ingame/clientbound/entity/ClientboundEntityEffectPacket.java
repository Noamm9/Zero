package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.entity.EntityEvent;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundEntityEffectPacket extends Packet {
    private int entityId;
    private @NonNull EntityEvent event;

    public ClientboundEntityEffectPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundEntityEffectPacket(Packet packet) {
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
