package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Animation;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundAnimatePacket extends Packet {
    private int entityId;
    private @Nullable Animation animation;

    public ClientboundAnimatePacket(ByteBuf data) {
        super(data);
    }

    public ClientboundAnimatePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.entityId = MinecraftTypes.readVarInt(in);
        this.animation = Animation.from(in.readUnsignedByte());
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.entityId);
        if (this.animation == null) {
            out.writeByte(-1); // Client does nothing on unknown ID
        } else {
            out.writeByte(this.animation.getId());
        }
    }
}
