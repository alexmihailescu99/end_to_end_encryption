package com.alexm.encryption_server.security;

import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class EncryptionManager {
    private PublicKey publicKey;

    public String publicKeyString;

    public void setPublicKeyString(String publicKeyString) {
        this.publicKeyString = publicKeyString;
        this.initFromStrings();
    };

    public void initFromStrings() {
        try {
            X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(decode(this.publicKeyString));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpecPublic);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    private static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
    private static byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    public String encrypt(String message) throws Exception {
        byte[] messageToBytes = message.getBytes();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(messageToBytes);
        return encode(encryptedBytes);
    }

//    public String decrypt(String encryptedMessage) throws Exception {
//        byte[] encryptedBytes = decode(encryptedMessage);
//        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//        cipher.init(Cipher.DECRYPT_MODE, privateKey);
//        byte[] decryptedMessage = cipher.doFinal(encryptedBytes);
//        return new String(decryptedMessage, "UTF8");
//    }

}