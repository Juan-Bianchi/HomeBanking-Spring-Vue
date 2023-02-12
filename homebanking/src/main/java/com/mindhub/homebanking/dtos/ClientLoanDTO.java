package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.ClientLoan;


public class ClientLoanDTO {
    long id;
    private double amount;
    private int payments;
    private long loanId;
    private String name;


    // CONSTRUCTORS

    public ClientLoanDTO(){};

    public ClientLoanDTO(ClientLoan clientLoan){
        this.id = clientLoan.getId();
        this.loanId = clientLoan.getLoan().getId();
        this.name = clientLoan.getLoan().getName();
        this.amount = clientLoan.getAmount();
        this.payments = clientLoan.getPayments();
    }


    // GETTER METHODS


    public long getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public int getPayments() {
        return payments;
    }

    public long getLoanId() {
        return loanId;
    }

    public String getName() {
        return name;
    }
}
