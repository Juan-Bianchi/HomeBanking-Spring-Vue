package com.mindhub.homebanking.services.implementations;

import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.TransactionDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.TransactionRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class TransactionServiceImplementation implements TransactionService {

    @Autowired
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final ClientService clientService;

    public TransactionServiceImplementation(TransactionRepository transactionRepository, AccountService accountService, ClientService clientService){
        this.clientService = clientService;
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }
    @Override
    public void save(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    @Override
    public void postTransactions(Double amount, String description, String origAccountNumb, String destAccountNumb, Authentication authentication) {
        Client client = clientService.findByEmail(authentication.getName());
        if(client == null){
            throw new RuntimeException("The origin account does not belong to the authenticated client.");
        }
        if(thereIsNullField(amount, description, origAccountNumb, destAccountNumb)){
            String errorMessage = verifyNullFields(amount, description, origAccountNumb, destAccountNumb);
            throw new RuntimeException(errorMessage);
        }
        Account originAccount = accountService.findAccountByNumber(origAccountNumb);
        if(!originAccount.getIsActive()){
            throw new RuntimeException("The origin account is not active, therefore not transactions can be made.");
        }
        if(originAccount.getBalance() < amount){
            throw new RuntimeException("The available balance is insufficient to complete the transaction.");
        }
        Account destinationAccount = accountService.findAccountByNumber(destAccountNumb);
        if(!destinationAccount.getIsActive()) {
            throw new RuntimeException("The destination account is not active. Therefore it cannot receive money.");
        }

        destinationAccount.setBalance(destinationAccount.getBalance() + amount);
        originAccount.setBalance(originAccount.getBalance() - amount);
        Transaction originMovement = new Transaction(TransactionType.DEBIT, amount * (-1), description + destAccountNumb, LocalDateTime.now());
        Transaction destinationMovement = new Transaction(TransactionType.CREDIT, amount, description + origAccountNumb, LocalDateTime.now());
        originAccount.addTransaction(originMovement);
        destinationAccount.addTransaction(destinationMovement);

        this.save(originMovement);
        this.save(destinationMovement);
        accountService.save(originAccount);
        accountService.save(destinationAccount);
    }

    @Override
    public List<TransactionDTO> getTransactions(String accountNumber, LocalDateTime fromDate, LocalDateTime thruDate) {
        Account account = accountService.findAccountByNumber(accountNumber);
        if(account == null){
            throw new RuntimeException("The account does not exist.");
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

        return transactions.map(TransactionDTO::new).collect(toList());
    }

    //AUXILIARY METHODS
    private boolean thereIsNullField(Double amount, String description, String origAccountNumb, String destAccountNumb){
        return amount == null || description.isEmpty() || origAccountNumb.isEmpty() || destAccountNumb.isEmpty();
    }

    private String verifyNullFields(Double amount, String description, String origAccountNumb, String destAccountNumb){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("⋆ Please, fill the following fields: ");
        boolean moreThanOne = false;

        if (amount == null){
            stringBuilder.append("amount");
            moreThanOne = true;
        }
        if (description.isEmpty()){
            if(moreThanOne){
                stringBuilder.append(", ");
            }
            else {
                moreThanOne = true;
            }
            stringBuilder.append("description");
        }
        if (origAccountNumb.isEmpty()){
            if(moreThanOne){
                stringBuilder.append(", ");
            }
            else {
                moreThanOne = true;
            }
            stringBuilder.append("origin account number");
        }
        if (destAccountNumb.isEmpty()){
            if(moreThanOne) {
                stringBuilder.append(", ");
            }
            stringBuilder.append("destination account number");
        }
        stringBuilder.append(".");

        return stringBuilder.toString();
    }

    private void verifyNotValidFields(Double amount, String description, String origAccountNumb, String destAccountNumb){
        if(origAccountNumb.equals(destAccountNumb)){
            throw new RuntimeException("The origin account number must be different to the destination account number.");
        }
        if(!accountService.existsAccountByNumber(origAccountNumb)){
            throw new RuntimeException("The origin account number does not exist.");
        }
        if(!accountService.existsAccountByNumber(destAccountNumb)) {
            throw new RuntimeException("The destination account number does not exist.");
        }
        if(amount < 1){
            throw new RuntimeException("The minimum transfer amount is one dollar.");
        }
    }
}
