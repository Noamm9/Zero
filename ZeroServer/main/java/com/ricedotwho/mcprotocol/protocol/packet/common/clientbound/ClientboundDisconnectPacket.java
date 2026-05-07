package com.ricedotwho.mcprotocol.protocol.packet.common.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundDisconnectPacket extends Packet {
    private @NonNull Component reason;

    public ClientboundDisconnectPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundDisconnectPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.reason = MinecraftTypes.readComponent(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeComponent(out, this.reason);
    }
}
