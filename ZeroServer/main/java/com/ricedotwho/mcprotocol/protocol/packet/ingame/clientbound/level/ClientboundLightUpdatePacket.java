package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.level.LightUpdateData;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundLightUpdatePacket extends Packet {
    private int x;
    private int z;
    private @NonNull LightUpdateData lightData;

    public ClientboundLightUpdatePacket(ByteBuf data) {
        super(data);
    }

    public ClientboundLightUpdatePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.x = MinecraftTypes.readVarInt(in);
        this.z = MinecraftTypes.readVarInt(in);
        this.lightData = MinecraftTypes.readLightUpdateData(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.x);
        MinecraftTypes.writeVarInt(out, this.z);
        MinecraftTypes.writeLightUpdateData(out, this.lightData);
    }
}
