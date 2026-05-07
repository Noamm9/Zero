package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.entity.metadata.GlobalPos;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundSetDefaultSpawnPositionPacket extends Packet {
    private GlobalPos globalPos;
    private float yaw;
    private float pitch;

    public ClientboundSetDefaultSpawnPositionPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundSetDefaultSpawnPositionPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.globalPos = MinecraftTypes.readGlobalPos(in);
        this.yaw = in.readFloat();
        this.pitch = in.readFloat();
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeGlobalPos(out, this.globalPos);
        out.writeFloat(this.yaw);
        out.writeFloat(this.pitch);
    }
}
