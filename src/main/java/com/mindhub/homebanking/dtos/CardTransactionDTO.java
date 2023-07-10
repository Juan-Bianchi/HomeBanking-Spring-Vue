package com.mindhub.homebanking.dtos;

public class CardTransactionDTO {

    private String number;
    private Integer cvv;
    private Double amount;
    private String description;


    //CONSTRUCTORS
    public CardTransactionDTO(){};

    public CardTransactionDTO(String number, Integer cvv, Double amount, String description){

        this.number = number;
        this.description = description;
        this.cvv = cvv;
        this.amount = amount;
    }


    //GETTER METHODS


    public String getNumber() {

        return number;
    }

    public Integer getCvv() {

        return cvv;
    }

    public Double getAmount() {

        return amount;
    }

    public String getDescription() {

        return description;
    }
}
