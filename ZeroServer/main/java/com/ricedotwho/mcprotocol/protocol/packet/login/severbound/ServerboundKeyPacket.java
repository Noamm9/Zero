package com.ricedotwho.mcprotocol.protocol.packet.login.severbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundKeyPacket extends Packet {
    private byte @NonNull [] sharedKey;
    private byte @NonNull [] encryptedChallenge;

    public ServerboundKeyPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundKeyPacket(Packet packet) {
        super(packet.getRawData());
    }


    public ServerboundKeyPacket(PublicKey publicKey, SecretKey secretKey, byte[] challenge) {
        this.sharedKey = runEncryption(Cipher.ENCRYPT_MODE, publicKey, secretKey.getEncoded());
        this.encryptedChallenge = runEncryption(Cipher.ENCRYPT_MODE, publicKey, challenge);
    }

    @Override
    public void decode(ByteBuf in) {
        this.sharedKey = MinecraftTypes.readByteArray(in);
        this.encryptedChallenge = MinecraftTypes.readByteArray(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeByteArray(out, this.sharedKey);
        MinecraftTypes.writeByteArray(out, this.encryptedChallenge);
    }

    public SecretKey getSecretKey(PrivateKey privateKey) {
        return new SecretKeySpec(runEncryption(Cipher.DECRYPT_MODE, privateKey, this.sharedKey), "AES");
    }

    public byte[] getEncryptedChallenge(PrivateKey privateKey) {
        return runEncryption(Cipher.DECRYPT_MODE, privateKey, this.encryptedChallenge);
    }

    private static byte[] runEncryption(int mode, Key key, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(key.getAlgorithm().equals("RSA") ? "RSA/ECB/PKCS1Padding" : "AES/CFB8/NoPadding");
            cipher.init(mode, key);
            return cipher.doFinal(data);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to " + (mode == Cipher.DECRYPT_MODE ? "decrypt" : "encrypt") + " data.", e);
        }
    }
}
