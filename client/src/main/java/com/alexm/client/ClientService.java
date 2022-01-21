package com.alexm.client;

import com.squareup.okhttp.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ClientService {
    private RSA rsa;
    private OkHttpClient client;
    private Boolean isRegistered = false;

    @Value("${my.server.url}")
    private String serverUrl;

    @Value("${my.client.name}")
    private String clientName;

    @Value("${server.port}")
    private Integer port;

    public ClientService(RSA rsa,
                         OkHttpClient client) {
        this.rsa = rsa;
        this.client = client;
    }

    // Register the client with the server on client startup
    @EventListener(ApplicationReadyEvent.class)
    public void registerClient() {
        this.registerOnServer();
        System.out.println("Registered on server as " + this.clientName);
        System.out.println("Public key is " + this.rsa.getKeys().get(0));
        System.out.println("Private key is " + this.rsa.getKeys().get(1));
    }

    public List<String> getEncryptedMessage() {
        if (!this.isRegistered)
            this.registerOnServer();


        this.rsa.initFromStrings();

        try {
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, this.clientName);
            Request request = new Request.Builder()
                    .url(serverUrl.concat("/server/secret"))
                    .post(body)
                    .build();

            Response response = this.client.newCall(request).execute();
            String encryptedMessage = response.body().string();
            String decryptedMessage = rsa.decrypt(encryptedMessage);

            return new ArrayList<>(Arrays.asList(encryptedMessage, decryptedMessage));
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
        }
    }

    public String registerOnServer() {
        this.rsa.init();

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        json.put("client", clientName);
        json.put("publicKey", this.rsa.getKeys().get(0));
        json.put("port", port.toString());

        RequestBody body = RequestBody.create(JSON, json.toString());

        Request request = new Request.Builder()
                .url(serverUrl.concat("/server"))
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            this.isRegistered = true;
            return response.body().string();
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    public String sendMessage(String messageBody) {
        JSONObject messageBodyJson = new JSONObject(messageBody);
        String message = messageBodyJson.getString("message");
        String receiver = messageBodyJson.getString("receiver");

        Request request = new Request.Builder()
                .url(serverUrl.concat("/server/").concat(receiver))
                .get()
                .build();
        try {
            Response response = this.client.newCall(request).execute();
            JSONObject clientDetailsJson = new JSONObject(response.body().string());
            ClientDetailsDto clientDetails = ClientDetailsDto.builder()
                    .publicKey(clientDetailsJson.getString("publicKey"))
                    .port(clientDetailsJson.getString("port"))
                    .build();

            this.rsa.setPublicKeyString(clientDetails.getPublicKey());

            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            JSONObject json = new JSONObject();
            json.put("message", this.rsa.encrypt(message));
            json.put("sender", this.clientName);
            RequestBody body = RequestBody.create(JSON, json.toString());

            request = new Request.Builder()
                    .url("http://localhost:".concat(clientDetails.getPort()).concat("/client/message"))
                    .post(body)
                    .build();

            try {
                response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException ex) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
        }
    }

    public String receiveMessage(String messageBody) {
        JSONObject jsonObject = new JSONObject(messageBody);
        String message = jsonObject.getString("message");
        String sender = jsonObject.getString("sender");

        System.out.println("Received message " + jsonObject.getString("message"));
        try {
            System.out.println("Decrypted message is " + this.rsa.decrypt(message) + " from " + sender);
            return this.rsa.decrypt(message);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }
}
