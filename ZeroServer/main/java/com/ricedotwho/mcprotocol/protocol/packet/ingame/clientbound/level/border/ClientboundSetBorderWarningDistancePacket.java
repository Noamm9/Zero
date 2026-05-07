package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level.border;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundSetBorderWarningDistancePacket extends Packet {
    private int warningBlocks;

    public ClientboundSetBorderWarningDistancePacket(ByteBuf data) {
        super(data);
    }

    public ClientboundSetBorderWarningDistancePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.warningBlocks = MinecraftTypes.readVarInt(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.warningBlocks);
    }
}
