package com.alexm.client;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client")
public class ClientController {
    ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<?> getSecret() {
        return new ResponseEntity<>(clientService.getEncryptedMessage(), HttpStatus.OK);
    }

    @GetMapping("/registerOnServer")
    public ResponseEntity<?> registerOnServer() {
        return new ResponseEntity<>(clientService.registerOnServer(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody String messageBody) {
        return new ResponseEntity<>(clientService.sendMessage(messageBody), HttpStatus.OK);
    }

    @PostMapping("/message")
    public ResponseEntity<?> receiveMessage(@RequestBody String messageBody) {
        return new ResponseEntity<>(clientService.receiveMessage(messageBody), HttpStatus.OK);
    }


}
