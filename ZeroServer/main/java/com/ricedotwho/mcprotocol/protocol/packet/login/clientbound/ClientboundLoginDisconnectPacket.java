package com.ricedotwho.mcprotocol.protocol.packet.login.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.DefaultComponentSerializer;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundLoginDisconnectPacket extends Packet {
    private static final int MAX_COMPONENT_STRING_LENGTH = 262144;

    private @NonNull Component reason;

    public ClientboundLoginDisconnectPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundLoginDisconnectPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        // uses the old json serialization rather than the 1.20.3 NBT serialization
        this.reason = DefaultComponentSerializer.get().deserialize(MinecraftTypes.readString(in, MAX_COMPONENT_STRING_LENGTH));
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeString(out, DefaultComponentSerializer.get().serialize(reason));
    }
}
