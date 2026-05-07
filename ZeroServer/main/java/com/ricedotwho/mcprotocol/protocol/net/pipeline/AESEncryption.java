package com.ricedotwho.mcprotocol.protocol.net.pipeline;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.security.GeneralSecurityException;
import java.security.Key;

public class AESEncryption implements PacketEncryption {
    private final Cipher inCipher = Cipher.getInstance("AES/CFB8/NoPadding");
    private final Cipher outCipher;

    public AESEncryption(Key key) throws GeneralSecurityException {
        this.inCipher.init(2, key, new IvParameterSpec(key.getEncoded()));
        this.outCipher = Cipher.getInstance("AES/CFB8/NoPadding");
        this.outCipher.init(1, key, new IvParameterSpec(key.getEncoded()));
    }

    public int getDecryptOutputSize(int length) {
        return this.inCipher.getOutputSize(length);
    }

    public int getEncryptOutputSize(int length) {
        return this.outCipher.getOutputSize(length);
    }

    public int decrypt(byte[] input, int inputOffset, int inputLength, byte[] output, int outputOffset) throws Exception {
        return this.inCipher.update(input, inputOffset, inputLength, output, outputOffset);
    }

    public int encrypt(byte[] input, int inputOffset, int inputLength, byte[] output, int outputOffset) throws Exception {
        return this.outCipher.update(input, inputOffset, inputLength, output, outputOffset);
    }
}