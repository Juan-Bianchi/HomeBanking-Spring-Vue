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

        return accountService.findAll().stream().map(account -> new AccountDTO(account)).collect(toSet());
    }

    @GetMapping("/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id) {

        return new AccountDTO(accountService.findById(id));
    }

    @GetMapping("/clients/current/accounts")
    public Set<AccountDTO> geAccounts(Authentication authentication){

       return clientService.findByEmail(authentication.getName()).getAccounts().stream().map(AccountDTO::new).collect(toSet());
    }

    @GetMapping("/clients/current/activeAccounts")
    public Set<AccountDTO> getActiveAccounts(Authentication authentication){

        return clientService.findByEmail(authentication.getName()).getAccounts().stream().filter(Account::getIsActive).map(AccountDTO::new).collect(toSet());
    }


    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> CreateAccount(Authentication authentication, @RequestParam AccountType accountType){

        Client client =  clientService.findByEmail(authentication.getName());
        if(client.getAccounts().stream().filter(Account::getIsActive).count() >= 3){

            return new ResponseEntity<>("The max amount of accounts have been already created", HttpStatus.FORBIDDEN);
        }

        String accountNumber = createAccountNumber(accountService);
        Account account = new Account(accountNumber, LocalDateTime.now(), 0, accountType);
        client.addAccount(account);
        accountService.save(account);
        clientService.save(client);
        AccountDTO accountDTO = new AccountDTO(account);

        return new ResponseEntity<>(accountDTO, HttpStatus.CREATED);

    }

    @PatchMapping("/clients/current/accounts")
    public ResponseEntity<Object> cancelAccount(@RequestParam String number, Authentication authentication){

        if(number.isEmpty()){

            return new ResponseEntity<>("Account number is empty. It must be provided.", HttpStatus.FORBIDDEN);
        }
        Client client =  clientService.findByEmail(authentication.getName());
        Account account = accountService.findAccountByNumber(number);
        if(account == null){

            return new ResponseEntity<>("The account does not exist.", HttpStatus.FORBIDDEN);
        }
        if(!client.getAccounts().contains(account)){

            return new ResponseEntity<>("This account does not belong to the current user.", HttpStatus.FORBIDDEN);
        }
        if(account.getBalance() > 0){

            return new ResponseEntity<>("You cannot delete an account if there is money in it.", HttpStatus.FORBIDDEN);
        }

        account.setIsActive(false);
        accountService.save(account);

        return new ResponseEntity<>("The account was cancelled", HttpStatus.ACCEPTED);
    }

}
