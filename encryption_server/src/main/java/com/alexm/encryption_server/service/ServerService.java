package com.alexm.encryption_server.service;

import com.alexm.encryption_server.dto.ClientDetailsDto;
import com.alexm.encryption_server.dto.ClientIdentifierDto;
import com.alexm.encryption_server.models.Client;
import com.alexm.encryption_server.repository.ClientRepository;
import com.alexm.encryption_server.security.EncryptionManager;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class ServerService {
    EncryptionManager encryptionManager;
    ClientRepository clientRepository;

    @Value("${my.message}")
    private String message;

    public ServerService(EncryptionManager encryptionManager,
                         ClientRepository clientRepository) {
        this.encryptionManager = encryptionManager;
        this.clientRepository = clientRepository;
    }

    public String getEncryptedSecret(String clientName) {
        try {
            Client client = clientRepository.findByClientName(clientName)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client could not be found"));
            this.encryptionManager.setPublicKeyString(client.getPublicKey());
            return encryptionManager.encrypt(message);
        } catch (Exception ex) {
            return "Error";
        }
    }

    public Long registerClient(ClientIdentifierDto clientIdentifierDto) {
        boolean invalid = clientIdentifierDto.getClient() == null || clientIdentifierDto.getClient().isEmpty()
                || clientIdentifierDto.getPublicKey() == null || clientIdentifierDto.getPublicKey().isEmpty();
        if (invalid)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid client registration JSON");

        Optional<Client> client = clientRepository.findByClientName(clientIdentifierDto.getClient());
        if (client.isPresent()) {
            Client presentClient = client.get();
            presentClient.setPublicKey(clientIdentifierDto.getPublicKey() != null
                    ? clientIdentifierDto.getPublicKey()
                    : presentClient.getPublicKey());
            return clientRepository.save(presentClient).getId();
        }

        return clientRepository.save(Client.builder()
                .clientName(clientIdentifierDto.getClient())
                .publicKey(clientIdentifierDto.getPublicKey())
                .port(clientIdentifierDto.getPort())
                .build()).getId();
    }

    public ClientDetailsDto getClientDetails(String clientName) {
        Client client = clientRepository.findByClientName(clientName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client not found"));
        return ClientDetailsDto.builder()
                .publicKey(client.getPublicKey())
                .port(client.getPort())
                .build();
    }
}
