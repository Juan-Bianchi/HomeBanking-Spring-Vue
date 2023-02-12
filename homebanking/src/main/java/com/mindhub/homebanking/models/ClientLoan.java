package com.mindhub.homebanking.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
public class ClientLoan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private double amount;
    private int payments;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    private Client client;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "loan_id")
    private Loan loan;

    //CONSTRUCTOR

    public ClientLoan(){};

    public ClientLoan(Client client, Loan loan, double amount, int payments){
        this.client = client;
        this.loan = loan;
        this.amount = amount;
        this.payments = payments;
    }


    // SETTER METHODS

    public void setAmount(double amount){
        this.amount = amount;
    }

    public void setPayments(int payments){
        this.payments = payments;
    }

    public void setClient(Client client){
        this.client = client;
    }

    public void setLoan(Loan loan){
        this.loan = loan;
    }

    // GETTER METHODS

    public long getId(){
        return this.id;
    }

    public int getPayments(){
        return this.payments;
    }

    public Client getClient(){
        return this.client;
    }

    public double getAmount(){
        return this.amount;
    }

    public Loan getLoan(){
        return this.loan;
    }

}
