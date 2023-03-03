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


    //CONTRUCTORS

    TransactionDTO(){}

    TransactionDTO(Transaction transaction){

        this.id = transaction.getId();
        this.type = transaction.getType();
        this.amount = transaction.getAmount();
        this.description = transaction.getDescription();
        this.date = transaction.getDate();
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
}
