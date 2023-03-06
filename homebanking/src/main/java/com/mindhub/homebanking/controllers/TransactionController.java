package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.TransactionDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransactionRepository transactionRepository;


    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> postTransactions( @RequestParam(required=false) Double amount, @RequestParam String description,
                                                    @RequestParam String origAccountNumb, @RequestParam String destAccountNumb,
                                                    Authentication authentication){

        Client client = clientRepository.findByEmail(authentication.getName());
        if(client == null){

            return new ResponseEntity<>("The origin account does not belong to the authenticated client.", HttpStatus.FORBIDDEN);
        }
        if(amount == null || description.isEmpty() || origAccountNumb.isEmpty() || destAccountNumb.isEmpty()){
            String errorMessage = "⋆ Please, fill the following fields: ";
            Boolean moreThanOne = false;

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
        if(!accountRepository.existsAccountByNumber(origAccountNumb)){

            return new ResponseEntity<>("The origin account number does not exist.", HttpStatus.FORBIDDEN);
        }
        if(!accountRepository.existsAccountByNumber(destAccountNumb)) {

            return new ResponseEntity<>("The destination account number does not exist.", HttpStatus.FORBIDDEN);
        }
        Account originAccount = accountRepository.findAccountByNumber(origAccountNumb);
        if(originAccount.getBalance() < amount){

            return new ResponseEntity<>("The available balance is insufficient to complete the transaction.", HttpStatus.FORBIDDEN);
        }
        if(amount < 1){
            return new ResponseEntity<>("The minimum transfer amount is one dollar.", HttpStatus.FORBIDDEN);
        }

        Transaction originMovement = new Transaction(TransactionType.DEBIT, amount * (-1), description + destAccountNumb, LocalDateTime.now());
        Transaction destinationMovement = new Transaction(TransactionType.CREDIT, amount, description + origAccountNumb, LocalDateTime.now());
        Account destinationAccount = accountRepository.findAccountByNumber(destAccountNumb);
        originAccount.addTransaction(originMovement);
        originAccount.setBalance(originAccount.getBalance() - amount);
        destinationAccount.addTransaction(destinationMovement);
        destinationAccount.setBalance(destinationAccount.getBalance() + amount);
        transactionRepository.save(originMovement);
        transactionRepository.save(destinationMovement);
        accountRepository.save(originAccount);
        accountRepository.save(destinationAccount);

        return new ResponseEntity<>("The transaction has been successfully completed.", HttpStatus.OK);
    }
}
