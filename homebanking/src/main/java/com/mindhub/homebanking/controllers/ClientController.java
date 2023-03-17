package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;

import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static com.mindhub.homebanking.utils.Utilitary.createAccountNumber;

@RestController
@RequestMapping("/api")
public class ClientController {


    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("/clients")
    public List<ClientDTO> getClients(){

        return clientService.findAll().stream().map(client -> new ClientDTO(client)).collect(toList());
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

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            String errorMessage = "⋆ Please, fill the following fields: ";
            boolean moreThanOne = false;

            if (firstName.isEmpty()){
                errorMessage += "first names";
                moreThanOne = true;
            }
            if (lastName.isEmpty()){
                if(moreThanOne){
                    errorMessage += ", ";
                }
                else {
                    moreThanOne = true;
                }
                errorMessage += "last names";
            }
            if (email.isEmpty()){
                if(moreThanOne){
                    errorMessage += ", ";
                }
                else {
                    moreThanOne = true;
                }
                errorMessage += "email";
            }
            if (password.isEmpty()){
                if(moreThanOne) {
                    errorMessage += ", ";
                }
                errorMessage += "password";
            }
            errorMessage += ".";
            return new ResponseEntity<>(errorMessage, HttpStatus.FORBIDDEN);
        }
        if (clientService.findByEmail(email) !=  null) {
            return new ResponseEntity<>("⋆ Email already in use", HttpStatus.FORBIDDEN);
        }

        Client client = new Client(firstName, lastName, email, passwordEncoder.encode(password));
        String accountNumber = createAccountNumber(accountService);
        Account account = new Account(accountNumber, LocalDateTime.now(), 0, AccountType.SAVINGS);
        client.addAccount(account);
        clientService.save(client);
        accountService.save(account);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }




}
