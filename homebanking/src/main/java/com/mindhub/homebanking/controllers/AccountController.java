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


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static utils.Utilitary.createAccountNumber;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClientRepository clientRepository;

    @RequestMapping("/accounts")
    public Set<AccountDTO> getAccounts() {
        return accountRepository.findAll().stream().map(account -> new AccountDTO(account)).collect(toSet());
    }

    @RequestMapping("/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id) {
        return accountRepository.findById(id).map(account-> new AccountDTO(account)).orElse(null);
    }

    @PostMapping("clients/current/accounts")
    public ResponseEntity<Object> CreateAccount(Authentication authentication){

        Client client =  clientRepository.findByEmail(authentication.getName());

        if(client.getAccounts().size() >= 3){
            return new ResponseEntity<>("The max amount of accounts have been already created", HttpStatus.FORBIDDEN);
        }

        String accountNumber = createAccountNumber(accountRepository);
        Account account = new Account(accountNumber, LocalDateTime.now(), 0);
        client.addAccount(account);
        accountRepository.save(account);

        return new ResponseEntity<>(HttpStatus.CREATED);

    }

}
