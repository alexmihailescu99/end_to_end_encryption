package com.alexm.encryption_server.utils;

import com.alexm.encryption_server.dto.ClientIdentifierDto;
import org.json.JSONObject;

public class Utils {
    public static ClientIdentifierDto mapJsonToDto(String clientIdentifierDto) {
        JSONObject jsonObject = new JSONObject(clientIdentifierDto);
        return ClientIdentifierDto.builder()
                .client(jsonObject.getString("client"))
                .publicKey(jsonObject.getString("publicKey"))
                .port(jsonObject.getString("port"))
                .build();
    }
}
