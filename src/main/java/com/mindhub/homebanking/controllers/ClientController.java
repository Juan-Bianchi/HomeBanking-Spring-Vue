package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;

import com.mindhub.homebanking.services.ClientService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class ClientController {

    private final ClientService clientService;
    public ClientController (ClientService clientService){
        this.clientService = clientService;
    }

    @GetMapping("/clients")
    public List<ClientDTO> getClients(){
        return clientService.findAll().stream().map(ClientDTO::new).collect(toList());
    }

    @GetMapping("clients/{id}")
    public ClientDTO getClient(@PathVariable Long id){
        return new ClientDTO(clientService.findById(id));
    }

    @GetMapping("/clients/current")
    public ClientDTO getClient(Authentication authentication) {
        return new ClientDTO(clientService.findByEmail(authentication.getName()));
    }

    @PostMapping("/clients")
    public ResponseEntity<Object> register(@RequestParam String firstName, @RequestParam String lastName,
                                           @RequestParam String email, @RequestParam String password) {
        try{
            clientService.register(firstName, lastName, email, password);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        catch (RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }


    @PostMapping("/clients/current/lastLogin")
    public ResponseEntity<?> updateLastLogin(@RequestParam String email, @RequestParam String newloginDate, @RequestParam String lastLoginDate){
        try{
            clientService.updateLastLogin(email, newloginDate, lastLoginDate);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
        catch (RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }


}
