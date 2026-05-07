package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundSelectAdvancementsTabPacket extends Packet {
    private @Nullable String tabId;

    public ClientboundSelectAdvancementsTabPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundSelectAdvancementsTabPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.tabId = MinecraftTypes.readNullable(in, MinecraftTypes::readString);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeNullable(out, this.tabId, MinecraftTypes::writeString);
    }
}
