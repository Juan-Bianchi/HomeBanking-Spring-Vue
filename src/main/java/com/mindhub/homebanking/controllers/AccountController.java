package com.mindhub.homebanking.controllers;


import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/api")
public class AccountController {

    private final AccountService accountService;
    private final ClientService clientService;

    public AccountController (AccountService accountService,@Lazy ClientService clientService){
        this.clientService = clientService;
        this.accountService = accountService;
    }

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
        return clientService.findByEmail(authentication.getName()).getAccounts().stream().filter(Account::getIsActive).map(AccountDTO::new).collect(toSet());
    }


    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> CreateAccount(Authentication authentication, @RequestParam AccountType accountType){
        try{
            return new ResponseEntity<>(accountService.createAccount(authentication, accountType), HttpStatus.CREATED);
        }
        catch(RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PatchMapping("/clients/current/accounts")
    public ResponseEntity<Object> cancelAccount(@RequestParam String number, Authentication authentication){
        try{
            accountService.cancelAccount(number, authentication);
            return new ResponseEntity<>("The account was cancelled", HttpStatus.ACCEPTED);
        }
        catch(RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

}
