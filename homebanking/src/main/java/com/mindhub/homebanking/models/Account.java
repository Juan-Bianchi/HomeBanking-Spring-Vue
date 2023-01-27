package com.mindhub.homebanking.models;

import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String number;
    private LocalDateTime creationDate;
    private double balance;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="client_id")
    private Client client;

    public Account(){}

    public Account( String number, LocalDateTime creationDate, double balance){
        this.number = number;
        this.balance = balance;
        this.creationDate = creationDate;
    }


    //METODOS SETTERS

    public void setNumber (String number){
        this.number = number;
    }

    public void setCreationDate (LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setBalance (double balance) {
        this.balance = balance;
    }

    public void setClient(Client client) {
        this.client = client;
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

    @JsonIgnore
    public Client getClient() {
        return this.client;
    }

    //OTROS METODOS
    @Override
    public String toString(){
        return
            "number: " + this.number + ",\n" +
            "creationDate: " + this.creationDate + ",\n" +
            "balance: " + this.balance + ",\n" +
            "id: " + this.id;
    }




}
