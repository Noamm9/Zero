package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.level;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundTeleportToEntityPacket extends Packet {
    private @NonNull UUID target;

    public ServerboundTeleportToEntityPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundTeleportToEntityPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.target = MinecraftTypes.readUUID(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeUUID(out, this.target);
    }
}
