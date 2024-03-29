package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Client;

import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class ClientDTO {

    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private Set<AccountDTO> accounts = new HashSet<>();
    private Set<ClientLoanDTO> loans = new HashSet<>();
    private Set<CardDTO> cards = new HashSet<>();
    private String lastLogin;
    private String newLogin;


    //CONSTRUCTORS
    public  ClientDTO(){}

    public ClientDTO(Client client) {

        this.id = client.getId();
        this.firstName = client.getFirstName();
        this.lastName = client.getLastName();
        this.email = client.getEmail();
        this.accounts = client.getAccounts().stream().map(account -> new AccountDTO(account)).collect(toSet());
        this.loans = client.getLoans().stream().map(loan -> new ClientLoanDTO(loan)).collect(toSet());
        this.cards = client.getCards().stream().map(card -> new CardDTO(card)).collect(toSet());
        this.lastLogin = client.getLastLogin();
        this.newLogin = client.getNewLogin();
    }

    //GETTER METHODS
    public String getFirstName() {

        return this.firstName;
    }

    public String getLastName(){

        return this.lastName;
    }

    public String getEmail(){

        return this.email;
    }

    public long getId(){

        return this.id;
    }

    public Set<AccountDTO> getAccounts(){

        return this.accounts;
    }

    public Set<ClientLoanDTO> getLoans(){

        return this.loans;
    }

    public Set<CardDTO> getCards(){

        return this.cards;
    }

    public String getLastLogin(){

        return this.lastLogin;
    }

    public String getNewLogin(){

        return this.newLogin;
    }

}
