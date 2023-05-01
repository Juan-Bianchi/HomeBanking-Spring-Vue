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

    @Autowired
    ClientService clientService;
    @Autowired
    AccountService accountService;
    @Autowired
    TransactionService transactionService;


    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions(@RequestParam String accountNumber, @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate, @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime thruDate){

        Account account = accountService.findAccountByNumber(accountNumber);
        if(account == null){

            return new ResponseEntity<>("The account does not exist.", HttpStatus.FORBIDDEN);
        }
        Stream<Transaction> transactions;
        if(fromDate != null && thruDate == null){
            transactions =  account.getTransactions().stream().filter(transaction -> transaction.getDate().isAfter(fromDate));
        }
        else if(fromDate == null && thruDate != null){
            transactions =  account.getTransactions().stream().filter(transaction -> transaction.getDate().isBefore(thruDate));
        }
        else if (fromDate != null) {
            transactions =  account.getTransactions().stream().filter(transaction -> transaction.getDate().isAfter(fromDate) && transaction.getDate().isBefore(thruDate));
        }
        else{
            transactions = account.getTransactions().stream();
        }

        List<TransactionDTO> transactionDTOS = transactions.map(TransactionDTO::new).collect(toList());

        return new ResponseEntity<>(transactionDTOS, HttpStatus.OK);
    }


    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> postTransactions( @RequestParam(required=false) Double amount, @RequestParam String description,
                                                    @RequestParam String origAccountNumb, @RequestParam String destAccountNumb,
                                                    Authentication authentication){

        Client client = clientService.findByEmail(authentication.getName());
        if(client == null){

            return new ResponseEntity<>("The origin account does not belong to the authenticated client.", HttpStatus.FORBIDDEN);
        }
        if(amount == null || description.isEmpty() || origAccountNumb.isEmpty() || destAccountNumb.isEmpty()){
            String errorMessage = "⋆ Please, fill the following fields: ";
            boolean moreThanOne = false;

            if (amount == null){
                errorMessage += "amount";
                moreThanOne = true;
            }
            if (description.isEmpty()){
                if(moreThanOne){
                    errorMessage += ", ";
                }
                else {
                    moreThanOne = true;
                }
                errorMessage += "description";
            }
            if (origAccountNumb.isEmpty()){
                if(moreThanOne){
                    errorMessage += ", ";
                }
                else {
                    moreThanOne = true;
                }
                errorMessage += "origin account number";
            }
            if (destAccountNumb.isEmpty()){
                if(moreThanOne) {
                    errorMessage += ", ";
                }
                errorMessage += "destination account number";
            }
            errorMessage += ".";

            return new ResponseEntity<>(errorMessage, HttpStatus.FORBIDDEN);
        }
        if(origAccountNumb.equals(destAccountNumb)){

            return new ResponseEntity<>("The origin account number must be different to the destination account number.", HttpStatus.FORBIDDEN);
        }
        if(!accountService.existsAccountByNumber(origAccountNumb)){

            return new ResponseEntity<>("The origin account number does not exist.", HttpStatus.FORBIDDEN);
        }
        if(!accountService.existsAccountByNumber(destAccountNumb)) {

            return new ResponseEntity<>("The destination account number does not exist.", HttpStatus.FORBIDDEN);
        }
        Account originAccount = accountService.findAccountByNumber(origAccountNumb);
        if(!originAccount.getIsActive()){

            return new ResponseEntity<>("The origin account is not active, therefore not transactions can be made.", HttpStatus.FORBIDDEN);
        }
        if(originAccount.getBalance() < amount){

            return new ResponseEntity<>("The available balance is insufficient to complete the transaction.", HttpStatus.FORBIDDEN);
        }
        if(amount < 1){

            return new ResponseEntity<>("The minimum transfer amount is one dollar.", HttpStatus.FORBIDDEN);
        }
        Account destinationAccount = accountService.findAccountByNumber(destAccountNumb);
        if(!destinationAccount.getIsActive()) {

            return new ResponseEntity<>("The destination account is not active. Therefore it cannot receive money.", HttpStatus.FORBIDDEN);
        }

        destinationAccount.setBalance(destinationAccount.getBalance() + amount);
        originAccount.setBalance(originAccount.getBalance() - amount);
        Transaction originMovement = new Transaction(TransactionType.DEBIT, amount * (-1), description + destAccountNumb, LocalDateTime.now());
        Transaction destinationMovement = new Transaction(TransactionType.CREDIT, amount, description + origAccountNumb, LocalDateTime.now());
        originAccount.addTransaction(originMovement);
        destinationAccount.addTransaction(destinationMovement);

        transactionService.save(originMovement);
        transactionService.save(destinationMovement);
        accountService.save(originAccount);
        accountService.save(destinationAccount);

        return new ResponseEntity<>("The transaction has been successfully completed.", HttpStatus.OK);
    }
}
