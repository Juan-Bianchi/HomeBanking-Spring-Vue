package com.mindhub.homebanking.models;



import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;


@Entity
public class Client {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String firstName;

    private String lastName;

    private String email;

    @OneToMany(mappedBy="client", fetch=FetchType.EAGER)
    private Set<Account> accounts = new HashSet<>();

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    private Set<ClientLoan> clientLoans = new HashSet<>();

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    private Set<Card> cards = new HashSet<>();

    private String password;



    //CONSTRUCTORS
    public  Client(){}

    public Client(String first, String last, String email, String password) {
        this.firstName = first;
        this.lastName = last;
        this.email = email;
        this.password = password;
    }


    //SPECIFIC METHODS
    @Override
    public String toString(){
        return
                "Client {\n\t" +
                        "id: " + this.id + ",\n\t" +
                        "firsName: " + this.firstName + ",\n\t" +
                        "lastName: " + this.lastName + ",\n\t" +
                        "email: " + this.email
                        +" \n}";
    }

    public void addAccount(Account account){
        account.setClient(this);
        accounts.add(account);
    }

    public void addClientLoan(ClientLoan clientLoan){
        clientLoan.setClient(this);
        this.clientLoans.add(clientLoan);
    }

    public void addCard(Card card){
        card.setClient(this);
        this.cards.add(card);
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

    @JsonIgnore
    public Set<Account> getAccounts() {
        return this.accounts;
    }

    public Set<ClientLoan> getLoans(){
        return this.clientLoans;
    }

    @JsonIgnore
    public Set<Loan> getLoan() {
        return clientLoans.stream().map(clientLoan -> clientLoan.getLoan()).collect(toSet());
    }

    @JsonIgnore
    public Set<Card> getCards(){
        return this.cards;
    }

    public String getPassword(){
        return this.password;
    }


    //SETTER METHODS

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void  setEmail(String email){
        this.email = email;
    }

    public void setPassWord(String password){
        this.password = password;
    }

}


