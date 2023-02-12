package com.mindhub.homebanking.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
    @OneToMany(mappedBy="account", fetch=FetchType.EAGER)
    private Set<Transaction> transactions = new HashSet<>();


    // CONSTRUCTORS

    public Account(){}

    public Account( String number, LocalDateTime creationDate, double balance){
        this.number = number;
        this.balance = balance;
        this.creationDate = creationDate;
    }


    // SETTER METHODS

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

    // GETTER METHODS
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

    public Client getClient() {
        return this.client;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }


    //OTHER METHODS
    @Override
    public String toString(){
        return

                "Account: { \n\t" +
                    "number: " + this.number + ",\n\t" +
                    "creationDate: " + this.creationDate + ",\n\t" +
                    "balance: " + this.balance + ",\n\t" +
                    "id: " + this.id;
    }

    public void addTransaction(Transaction transaction) {
        transaction.setAccount(this);
        transactions.add(transaction);
    }




}
