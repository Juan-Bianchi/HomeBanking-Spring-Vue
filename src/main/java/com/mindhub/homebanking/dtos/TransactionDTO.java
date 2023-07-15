package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;

import java.time.LocalDateTime;

public class TransactionDTO {

    private long id;
    private TransactionType type;
    private double amount;
    private String description;
    private LocalDateTime date;
    private double accountBalance;


    //CONTRUCTORS

    public TransactionDTO(){}

    public TransactionDTO(Transaction transaction){

        this.id = transaction.getId();
        this.type = transaction.getType();
        this.amount = transaction.getAmount();
        this.description = transaction.getDescription();
        this.date = transaction.getDate();
        this.accountBalance = transaction.getAccountBalance();
    }

    //GETTER METHODS


    public long getId() {

        return id;
    }

    public TransactionType getType() {

        return type;
    }

    public double getAmount() {

        return amount;
    }

    public String getDescription() {

        return description;
    }

    public LocalDateTime getDate() {

        return date;
    }

    public double getAccountBalance(){

        return  accountBalance;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TransactionDTO other = (TransactionDTO) obj;
        return this.id == other.id && this.description == other.description && this.date == other.date;
    }
}
