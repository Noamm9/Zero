package com.ricedotwho.mcprotocol.protocol.packet.login.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.auth.GameProfile;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundLoginFinishedPacket extends Packet {
    private @NonNull GameProfile profile;

    public ClientboundLoginFinishedPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundLoginFinishedPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.profile = MinecraftTypes.readStaticGameProfile(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeStaticGameProfile(out, profile);
    }
}
