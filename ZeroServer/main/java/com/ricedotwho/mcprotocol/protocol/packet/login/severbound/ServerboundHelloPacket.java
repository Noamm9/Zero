package com.ricedotwho.mcprotocol.protocol.packet.login.severbound;

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
public class ServerboundHelloPacket extends Packet {
    private @NonNull String username;
    private @NonNull UUID profileId;

    public ServerboundHelloPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundHelloPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.username = MinecraftTypes.readString(in);
        this.profileId = MinecraftTypes.readUUID(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeString(out, this.username);
        MinecraftTypes.writeUUID(out, this.profileId);
    }
}
