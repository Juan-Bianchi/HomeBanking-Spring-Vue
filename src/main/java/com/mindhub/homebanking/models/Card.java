package com.mindhub.homebanking.models;


import com.mindhub.homebanking.repositories.CardRepository;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.HOURS;

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
    private Boolean isActive;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    private Client client;


    // CONSTRUCTORS

    public Card(){}

    public Card(CardType type, CardColor color,String cardHolder, String number, int cvv, LocalDate fromDate, LocalDate thruDate) {

        this.type = type;
        this.color = color;
        this.cardHolder = cardHolder;
        this.number = number;
        this.cvv = cvv;
        this.fromDate = fromDate;
        this.thruDate = thruDate;
        this.isActive = true;
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
    }

    public void setIsActive(Boolean bool){

        this.isActive = bool;
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

    public Boolean getIsActive(){

        return isActive;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Card other = (Card) obj;
        return this.id == other.id;
    }
}
