package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.debug;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.debug.DebugInfo;
import org.geysermc.mcprotocollib.protocol.data.game.debug.DebugSubscriptions;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundDebugEventPacket extends Packet {
    private DebugSubscriptions subscriptionType;
    private DebugInfo subscription;

    public ClientboundDebugEventPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundDebugEventPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.subscriptionType = DebugSubscriptions.from(MinecraftTypes.readVarInt(in));
        this.subscription = MinecraftTypes.readDebugSubscription(in, this.subscriptionType);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.subscriptionType.ordinal());
        MinecraftTypes.writeDebugSubscription(out, this.subscriptionType, this.subscription);
    }
}
