package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.debug;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.debug.RemoteDebugSampleType;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundDebugSamplePacket extends Packet {
    private long[] sample;
    private RemoteDebugSampleType debugSampleType;

    public ClientboundDebugSamplePacket(ByteBuf data) {
        super(data);
    }

    public ClientboundDebugSamplePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.sample = MinecraftTypes.readLongArray(in);
        this.debugSampleType = RemoteDebugSampleType.from(MinecraftTypes.readVarInt(in));
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeLongArray(out, this.sample);
        MinecraftTypes.writeVarInt(out, this.debugSampleType.ordinal());
    }
}
