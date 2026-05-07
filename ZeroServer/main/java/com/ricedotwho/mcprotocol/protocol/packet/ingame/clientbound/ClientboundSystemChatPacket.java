package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound;

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
public class ClientboundSystemChatPacket extends Packet {
    private Component content;
    private boolean overlay;

    public ClientboundSystemChatPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundSystemChatPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.content = MinecraftTypes.readComponent(in);
        this.overlay = in.readBoolean();
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeComponent(out, this.content);
        out.writeBoolean(this.overlay);
    }
}
