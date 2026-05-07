package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.entity.Effect;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundRemoveMobEffectPacket extends Packet {
    private int entityId;
    private @NonNull Effect effect;

    public ClientboundRemoveMobEffectPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundRemoveMobEffectPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.entityId = MinecraftTypes.readVarInt(in);
        this.effect = MinecraftTypes.readEffect(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.entityId);
        MinecraftTypes.writeEffect(out, this.effect);
    }
}
