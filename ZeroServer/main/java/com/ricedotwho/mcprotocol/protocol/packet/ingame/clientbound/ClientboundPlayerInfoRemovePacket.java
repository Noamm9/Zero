package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundPlayerInfoRemovePacket extends Packet {
    private List<UUID> profileIds;

    public ClientboundPlayerInfoRemovePacket(ByteBuf data) {
        super(data);
    }

    public ClientboundPlayerInfoRemovePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.profileIds = MinecraftTypes.readList(in, MinecraftTypes::readUUID);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeList(out, this.profileIds, MinecraftTypes::writeUUID);
    }
}
