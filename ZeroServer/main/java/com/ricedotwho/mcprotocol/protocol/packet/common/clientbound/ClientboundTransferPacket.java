package com.ricedotwho.mcprotocol.protocol.packet.common.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundTransferPacket extends Packet {
    private String host;
    private int port;

    public ClientboundTransferPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundTransferPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.host = MinecraftTypes.readString(in);
        this.port = MinecraftTypes.readVarInt(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeString(out, this.host);
        MinecraftTypes.writeVarInt(out, this.port);
    }
}
