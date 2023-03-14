package com.mindhub.homebanking.controllers;


import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static com.mindhub.homebanking.utils.Utilitary.createAccountNumber;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/accounts")
    public Set<AccountDTO> getAccounts() {

        return accountRepository.findAll().stream().map(account -> new AccountDTO(account)).collect(toSet());
    }

    @GetMapping("/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id) {

        return accountRepository.findById(id).map(account-> new AccountDTO(account)).orElse(null);
    }

    @GetMapping("/clients/current/accounts")
    public Set<AccountDTO> getCurrentAccounts(Authentication authentication){

       return clientRepository.findByEmail(authentication.getName()).getAccounts().stream().map(account -> new AccountDTO(account)).collect(toSet());
    }


    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> CreateAccount(Authentication authentication){

        Client client =  clientRepository.findByEmail(authentication.getName());

        if(client.getAccounts().size() >= 3){
            return new ResponseEntity<>("The max amount of accounts have been already created", HttpStatus.FORBIDDEN);
        }

        String accountNumber = createAccountNumber(accountRepository);
        Account account = new Account(accountNumber, LocalDateTime.now(), 0);
        client.addAccount(account);
        accountRepository.save(account);
        AccountDTO accountDTO = new AccountDTO(account);

        return new ResponseEntity<>(accountDTO, HttpStatus.CREATED);

    }

}
