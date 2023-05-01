package com.mindhub.homebanking.dtos;

public class LoanApplicationDTO {

    Long idLoan;
    Double amount;
    Integer payments;
    String associatedAccountNumber;


    //CONSTRUCTOR
    LoanApplicationDTO(){}

    LoanApplicationDTO(Long id, Double amount, Integer payments, String associatedAccountNumber){
        this.idLoan = id;
        this.amount = amount;
        this.payments = payments;
        this.associatedAccountNumber = associatedAccountNumber;
    }


    //GETTER METHODS


    public Long getIdLoan() {
        return idLoan;
    }

    public Double getAmount() {
        return amount;
    }

    public Integer getPayments() {
        return payments;
    }

    public String getAssociatedAccountNumber() {
        return associatedAccountNumber;
    }



}
