package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.debug;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.debug.DebugInfo;
import org.geysermc.mcprotocollib.protocol.data.game.debug.DebugSubscriptions;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundDebugEntityValuePacket extends Packet {
    private int entityId;
    private DebugSubscriptions subscriptionType;
    private @Nullable DebugInfo subscription;

    public ClientboundDebugEntityValuePacket(ByteBuf data) {
        super(data);
    }

    public ClientboundDebugEntityValuePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.entityId = MinecraftTypes.readVarInt(in);
        this.subscriptionType = DebugSubscriptions.from(MinecraftTypes.readVarInt(in));
        this.subscription = MinecraftTypes.readDebugSubscriptionUpdate(in, this.subscriptionType);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.entityId);
        MinecraftTypes.writeVarInt(out, this.subscriptionType.ordinal());
        MinecraftTypes.writeDebugSubscriptionUpdate(out, this.subscriptionType, this.subscription);
    }
}
