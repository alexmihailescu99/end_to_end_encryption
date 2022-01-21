package com.alexm.encryption_server.controller;

import com.alexm.encryption_server.dto.ClientDetailsDto;
import com.alexm.encryption_server.service.ServerService;
import com.alexm.encryption_server.utils.Utils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/server")
public class ServerController {
    ServerService serverService;

    public ServerController(ServerService serverService) {
        this.serverService = serverService;
    }

    @PostMapping("/secret")
    public String getEncryptedSecret(@RequestBody String clientName){
        return serverService.getEncryptedSecret(clientName);
    }

    @PostMapping
    public Long registerClient(@RequestBody String clientIdentifierDto) { return serverService.registerClient(Utils.mapJsonToDto(clientIdentifierDto)); }

    @GetMapping("/{clientName}")
    public ClientDetailsDto getClientDetails(@PathVariable String clientName) { return serverService.getClientDetails(clientName); }
}
