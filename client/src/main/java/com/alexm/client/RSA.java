package com.alexm.client;

import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;


public class RSA {

    private PrivateKey privateKey;
    private PublicKey publicKey;

    private String privateKeyString;
    private String publicKeyString;

    public void setPublicKeyString(String publicKeyString) {
        this.publicKeyString = publicKeyString;
        this.initFromStrings();
    };

    // Returns a list of <PrivateKey, PublicKey>
    public List<String> init() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            KeyPair pair = generator.generateKeyPair();
            this.privateKey = pair.getPrivate();
            this.publicKey = pair.getPublic();

            this.privateKeyString = encode(pair.getPrivate().getEncoded());
            this.publicKeyString = encode(pair.getPublic().getEncoded());

            return new ArrayList<>(Arrays.asList(this.publicKey.toString(), this.privateKey.toString()));

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }

    public void initFromStrings() {
        try {
            X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(decode(this.publicKeyString));
            PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(decode(this.privateKeyString));

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            this.publicKey = keyFactory.generatePublic(keySpecPublic);
            this.privateKey = keyFactory.generatePrivate(keySpecPrivate);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }


    public List<String> getKeys() {
        return new ArrayList<>(Arrays.asList(encode(this.publicKey.getEncoded()), encode(this.privateKey.getEncoded())));
    }

    public String encrypt(String message) throws Exception {
        byte[] messageToBytes = message.getBytes();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
        byte[] encryptedBytes = cipher.doFinal(messageToBytes);
        return encode(encryptedBytes);
    }

    private static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private static byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    public String decrypt(String encryptedMessage) throws Exception {
        byte[] encryptedBytes = decode(encryptedMessage);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, this.privateKey);
        byte[] decryptedMessage = cipher.doFinal(encryptedBytes);
        return new String(decryptedMessage, "UTF8");
    }

}