package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Loan;

import java.util.List;

public class LoanCreationDTO {

    private String name;
    private Double maxAmount;
    private List<Integer> payments;
    private Double interestRate;


    // CONSTRUCTORS

    public LoanCreationDTO(){}

    public LoanCreationDTO(Loan loan){

        this.name = loan.getName();
        this.maxAmount = loan.getMaxAmount();
        this.payments = loan.getPayments();
        this.interestRate = loan.getInterestRate();
    }


    //GETTER METHODS


    public String getName() {
        return name;
    }

    public Double getMaxAmount() {
        return maxAmount;
    }

    public List<Integer> getPayments() {
        return payments;
    }

    public Double getInterestRate(){

        return interestRate;
    }

}
