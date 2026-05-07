package com.ricedotwho.mcprotocol.protocol.packet.common.severbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.ResourcePackStatus;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundResourcePackPacket extends Packet {
    private @NonNull UUID resourceId;
    private @NonNull ResourcePackStatus status;

    public ServerboundResourcePackPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundResourcePackPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.resourceId = MinecraftTypes.readUUID(in);
        this.status = ResourcePackStatus.from(MinecraftTypes.readVarInt(in));
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeUUID(out, resourceId);
        MinecraftTypes.writeVarInt(out, this.status.ordinal());
    }
}
