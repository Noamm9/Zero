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
public class ClientboundDebugChunkValuePacket extends Packet {
    private int chunkX;
    private int chunkZ;
    private DebugSubscriptions subscriptionType;
    private @Nullable DebugInfo subscription;

    public ClientboundDebugChunkValuePacket(ByteBuf data) {
        super(data);
    }

    public ClientboundDebugChunkValuePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        long chunkPos = in.readLong();
        this.chunkX = (int)chunkPos;
        this.chunkZ = (int)(chunkPos >> 32);

        this.subscriptionType = DebugSubscriptions.from(MinecraftTypes.readVarInt(in));
        this.subscription = MinecraftTypes.readDebugSubscriptionUpdate(in, this.subscriptionType);
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeLong(this.chunkX & 0xffffffffL | (this.chunkZ & 0xffffffffL) << 32);
        MinecraftTypes.writeVarInt(out, this.subscriptionType.ordinal());
        MinecraftTypes.writeDebugSubscriptionUpdate(out, this.subscriptionType, this.subscription);
    }
}
