package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundChatSessionUpdatePacket extends Packet {
    private UUID sessionId;
    private long expiresAt;
    private PublicKey publicKey;
    private byte[] keySignature;

    public ServerboundChatSessionUpdatePacket(ByteBuf data) {
        super(data);
    }

    public ServerboundChatSessionUpdatePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.sessionId = MinecraftTypes.readUUID(in);
        this.expiresAt = in.readLong();
        byte[] keyBytes = MinecraftTypes.readByteArray(in);
        this.keySignature = MinecraftTypes.readByteArray(in);

        PublicKey publicKey;
        try {
            publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Could not decode public key.", e);
        }

        this.publicKey = publicKey;
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeUUID(out, this.sessionId);
        out.writeLong(this.expiresAt);
        MinecraftTypes.writeByteArray(out, this.publicKey.getEncoded());
        MinecraftTypes.writeByteArray(out, this.keySignature);
    }
}
