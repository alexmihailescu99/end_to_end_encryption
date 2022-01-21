package com.alexm.encryption_server.repository;

import com.alexm.encryption_server.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByClientName(String clientName);
    Optional<Client> findByPublicKey(String publicKey);
}
