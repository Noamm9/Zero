package com.ricedotwho.mcprotocol.utils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class CryptUtil {

    public static SecretKey generateSharedKey() {
        try {
            KeyGenerator gen = KeyGenerator.getInstance("AES");
            gen.init(128);
            return gen.generateKey();
        } catch(NoSuchAlgorithmException e) {
            throw new Error("Failed to generate shared key.", e);
        }
    }

    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(1024);
            return gen.generateKeyPair();
        } catch(NoSuchAlgorithmException e) {
            throw new Error("Failed to generate key pair.", e);
        }
    }

}
