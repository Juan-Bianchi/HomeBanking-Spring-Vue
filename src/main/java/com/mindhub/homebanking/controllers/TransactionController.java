package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.TransactionDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final ClientService clientService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    public TransactionController(ClientService clientService, AccountService accountService, TransactionService transactionService){
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.clientService = clientService;
    }


    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions(@RequestParam String accountNumber, @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate, @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime thruDate){
        try {
            return new ResponseEntity<>(transactionService.getTransactions(accountNumber, fromDate, thruDate), HttpStatus.OK);
        }
        catch (RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }


    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> postTransactions( @RequestParam(required=false) Double amount, @RequestParam String description,
                                                    @RequestParam String origAccountNumb, @RequestParam String destAccountNumb,
                                                    Authentication authentication){
        try{
            transactionService.postTransactions(amount, description, origAccountNumb, destAccountNumb, authentication);
            return new ResponseEntity<>("The transaction has been successfully completed.", HttpStatus.OK);
        }
        catch (RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }

    }
}
