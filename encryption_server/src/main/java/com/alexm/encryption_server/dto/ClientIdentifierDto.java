package com.alexm.encryption_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ClientIdentifierDto {
    private String client;
    private String publicKey;
    private String port;
}
