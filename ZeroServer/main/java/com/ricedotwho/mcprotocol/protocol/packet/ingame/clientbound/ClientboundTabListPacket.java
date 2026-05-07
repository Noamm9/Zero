package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundTabListPacket extends Packet {
    private @NonNull Component header;
    private @NonNull Component footer;

    public ClientboundTabListPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundTabListPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.header = MinecraftTypes.readComponent(in);
        this.footer = MinecraftTypes.readComponent(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeComponent(out, this.header);
        MinecraftTypes.writeComponent(out, this.footer);
    }
}
