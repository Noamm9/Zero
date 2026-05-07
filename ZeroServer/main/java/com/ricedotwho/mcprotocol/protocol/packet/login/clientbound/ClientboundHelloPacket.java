package com.ricedotwho.mcprotocol.protocol.packet.login.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundHelloPacket extends Packet {
    private @NonNull String serverId;
    private @NonNull PublicKey publicKey;
    private byte @NonNull [] challenge;
    private boolean shouldAuthenticate;

    public ClientboundHelloPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundHelloPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.serverId = MinecraftTypes.readString(in);
        byte[] publicKey = MinecraftTypes.readByteArray(in);
        this.challenge = MinecraftTypes.readByteArray(in);
        this.shouldAuthenticate = in.readBoolean();

        try {
            this.publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKey));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Could not decode public key.", e);
        }
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeString(out, this.serverId);
        byte[] encoded = this.publicKey.getEncoded();
        MinecraftTypes.writeByteArray(out, encoded);
        MinecraftTypes.writeByteArray(out, this.challenge);
        out.writeBoolean(this.shouldAuthenticate);
    }
}
