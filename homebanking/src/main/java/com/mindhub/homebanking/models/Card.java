package com.mindhub.homebanking.models;


import com.mindhub.homebanking.repositories.CardRepository;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String cardHolder;
    private CardType type;
    private CardColor color;
    private String number;
    private int cvv;
    private LocalDate fromDate;
    private LocalDate thruDate;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    private Client client;


    // CONSTRUCTORS

    public Card(){}

    public Card(CardType type, CardColor color, String number, int cvv, LocalDate fromDate, LocalDate thruDate) {
        this.type = type;
        this.color = color;
        this.number = number;
        this.cvv = cvv;
        this.fromDate = fromDate;
        this.thruDate = thruDate;
    }


    // OTHER METHODS
/*
   public String generateNumber(){

        boolean isAlreadyCreated;
        String numb;

        do{
            String firstNumber = String.format("%04d", (int)(Math.random() * 9999));
            String secondNumber = String.format("%04d", (int)(Math.random() * 9999));
            String thirdNumber = String.format("%04d", (int)(Math.random() * 9999));
            String fourthNumber = String.format("%04d", (int)(Math.random() * 9999));
            numb = firstNumber + "-" + secondNumber + "-" + thirdNumber + "-" + fourthNumber;

            isAlreadyCreated = this.verifyNumber(numb);
        } while(isAlreadyCreated);

        return numb;
    }


    public boolean verifyNumber(String number){
        return cardRepository.findAll().stream().anyMatch(card -> card.getNumber().equals(number));
    }*/

    public int createCVV(){
        int cvv = (int)(Math.random() * 999);
        while(cvv == 0){
            cvv = (int)(Math.random() * 999);
        }
        return cvv;
    }

    // SETTER METHODS

    public void setType(CardType type){
        this.type = type;
    }

    public void setColor(CardColor color){
        this.color = color;
    }

    public void setNumber(String number){
        this.number = number;
    }

    public void setCvv(int cvv){
        this.cvv = cvv;
    }

    public void setFromDate(LocalDate fromDate){
        this.fromDate = fromDate;
    }

    public void setThruDate(LocalDate thruDate){
        this.thruDate = thruDate;
    }

    public void setClient(Client client){
        this.client = client;
        this.cardHolder = client.getFirstName() + " " + client.getLastName();
    }


    // GETTER METHODS


    public long getId() {
        return id;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public CardType getType() {
        return type;
    }

    public CardColor getColor() {
        return color;
    }

    public String getNumber() {
        return number;
    }

    public int getCvv() {
        return cvv;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getThruDate() {
        return thruDate;
    }

    public Client getClient() {
        return client;
    }
}
