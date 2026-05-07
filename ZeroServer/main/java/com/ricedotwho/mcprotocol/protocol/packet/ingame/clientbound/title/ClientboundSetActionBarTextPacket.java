package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.title;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundSetActionBarTextPacket extends Packet {
    private Component text;

    public ClientboundSetActionBarTextPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundSetActionBarTextPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.text = MinecraftTypes.readComponent(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeComponent(out, this.text);
    }
}
