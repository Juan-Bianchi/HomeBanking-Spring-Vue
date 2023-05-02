package com.mindhub.homebanking.controllers;


import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static com.mindhub.homebanking.utils.AccountUtils.createAccountNumber;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    AccountService accountService;

    @Autowired
    ClientService clientService;

    @GetMapping("/accounts")
    public Set<AccountDTO> getAccounts() {

        return accountService.findAll();
    }

    @GetMapping("/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id) {

        return new AccountDTO(accountService.findById(id));
    }

    @GetMapping("/clients/current/accounts")
    public Set<AccountDTO> getAccounts(Authentication authentication){

        return clientService.getAccountsDTO(clientService.findByEmail(authentication.getName()));
    }

    @GetMapping("/clients/current/activeAccounts")
    public Set<AccountDTO> getActiveAccounts(Authentication authentication){

        return clientService.getAccountsDTO(clientService.findByEmail(authentication.getName()));
    }


    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> CreateAccount(Authentication authentication, @RequestParam AccountType accountType){

        return accountService.createAccount(authentication, accountType);
    }

    @PatchMapping("/clients/current/accounts")
    public ResponseEntity<Object> cancelAccount(@RequestParam String number, Authentication authentication){

        return accountService.cancelAccount(number, authentication);
    }

}
