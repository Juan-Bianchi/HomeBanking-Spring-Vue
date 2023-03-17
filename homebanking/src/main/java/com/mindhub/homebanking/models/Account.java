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
    Boolean isActive;
    AccountType type;


    // CONSTRUCTORS

    public Account(){}

    public Account( String number, LocalDateTime creationDate, double balance, AccountType accountType){

        this.number = number;
        this.balance = balance;
        this.creationDate = creationDate;
        this.isActive = true;
        this.type = accountType;
    }


    //OTHER METHODS
    @Override
    public String toString(){

        return "Account: { \n\t" +
                        "number: " + this.number + ",\n\t" +
                        "creationDate: " + this.creationDate + ",\n\t" +
                        "balance: " + this.balance + ",\n\t" +
                        "id: " + this.id;
    }

    public void addTransaction(Transaction transaction) {
        transaction.setAccountBalance(this.getBalance());
        transaction.setAccount(this);
        transactions.add(transaction);
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

    public void setIsActive(Boolean isActive){

        this.isActive = isActive;
    }

    public void setType(AccountType accountType){

        this.type = accountType;
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

    public Boolean getIsActive(){

        return isActive;
    }

    public AccountType getType(){

        return type;
    }
}
