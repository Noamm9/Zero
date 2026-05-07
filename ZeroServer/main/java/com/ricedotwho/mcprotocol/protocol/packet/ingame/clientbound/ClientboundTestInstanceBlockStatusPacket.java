package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.cloudburstmc.math.vector.Vector3i;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundTestInstanceBlockStatusPacket extends Packet {
    private Component status;
    private @Nullable Vector3i size;

    public ClientboundTestInstanceBlockStatusPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundTestInstanceBlockStatusPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.status = MinecraftTypes.readComponent(in);
        this.size = MinecraftTypes.readNullable(in, MinecraftTypes::readVec3i);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeComponent(out, this.status);
        MinecraftTypes.writeNullable(out, this.size, MinecraftTypes::writeVec3i);
    }
}
