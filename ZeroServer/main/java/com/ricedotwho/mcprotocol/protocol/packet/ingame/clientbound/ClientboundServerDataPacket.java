package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundServerDataPacket extends Packet {
    private Component motd;
    private byte @Nullable [] iconBytes;

    public ClientboundServerDataPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundServerDataPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.motd = MinecraftTypes.readComponent(in);
        this.iconBytes = MinecraftTypes.readNullable(in, MinecraftTypes::readByteArray);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeComponent(out, this.motd);
        MinecraftTypes.writeNullable(out, this.iconBytes, MinecraftTypes::writeByteArray);
    }
}
