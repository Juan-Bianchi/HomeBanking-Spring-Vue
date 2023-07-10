package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class AccountDTO {

    private long id;
    private String number;
    private LocalDateTime creationDate;
    private double balance;
    private Set<TransactionDTO> transactions = new HashSet<>();
    private Boolean isActive;
    private AccountType type;


    public AccountDTO(){}

    public AccountDTO( Account account){

        this.id = account.getId();
        this.number = account.getNumber();
        this.balance = account.getBalance();
        this.creationDate = account.getCreationDate();
        this.transactions = account.getTransactions().stream().map(TransactionDTO::new).collect(toSet());
        this.isActive = account.getIsActive();
        this.type = account.getType();
    }


    //METODOS GETTERS

    public long getId(){

        return this.id;
    }

    public String getNumber(){

        return  this.number;
    }

    public double getBalance(){

        return this.balance;
    }

    public LocalDateTime getCreationDate(){

        return this.creationDate;
    }

    public Set<TransactionDTO> getTransactions(){

        return this.transactions;
    }

    public AccountType getType(){

        return this.type;
    }

}
