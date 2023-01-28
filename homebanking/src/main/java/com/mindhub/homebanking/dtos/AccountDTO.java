package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Account;
import java.time.LocalDateTime;

public class AccountDTO {
    private long id;
    private String number;
    private LocalDateTime creationDate;
    private double balance;


    public AccountDTO(){}

    public AccountDTO( Account account){
        this.number = account.getNumber();
        this.balance = account.getBalance();
        this.creationDate = account.getCreationDate();
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

}
