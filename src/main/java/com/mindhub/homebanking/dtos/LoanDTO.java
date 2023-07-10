package com.mindhub.homebanking.dtos;


import com.mindhub.homebanking.models.Loan;
import java.util.List;

public class LoanDTO {

    private long id;
    private String name;
    private double maxAmount;
    private List<Integer> payments;
    private double interestRate;


    // CONSTRUCTORS

    public LoanDTO(){}

    public LoanDTO(Loan loan){

        this.id = loan.getId();
        this.name = loan.getName();
        this.maxAmount = loan.getMaxAmount();
        this.payments = loan.getPayments();
        this.interestRate = loan.getInterestRate();
    }


    //GETTER METHODS


    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getMaxAmount() {
        return maxAmount;
    }

    public List<Integer> getPayments() {
        return payments;
    }

    public double getInterestRate(){
        return interestRate;
    }
}
