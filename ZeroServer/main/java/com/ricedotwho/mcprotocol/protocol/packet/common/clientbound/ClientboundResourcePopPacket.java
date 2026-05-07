package com.ricedotwho.mcprotocol.protocol.packet.common.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundResourcePopPacket extends Packet {
    private @Nullable UUID resourceId;

    public ClientboundResourcePopPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundResourcePopPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.resourceId = MinecraftTypes.readNullable(in, MinecraftTypes::readUUID);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeNullable(out, this.resourceId, MinecraftTypes::writeUUID);
    }
}
