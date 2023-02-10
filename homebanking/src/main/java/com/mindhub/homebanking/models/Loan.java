package com.mindhub.homebanking.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Entity
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String name;
    private double maxAmount;
    @ElementCollection
    @Column(name="payments")
    private List<Integer> payments = new ArrayList<>();
    @OneToMany(mappedBy = "loan", fetch = FetchType.EAGER)
    private Set<ClientLoan> clientLoans = new HashSet<>();


    //CONSTRUCTORS

    public Loan(){};

    public Loan(String name, double maxAmount, List<Integer> payments){
        this.name = name;
        this.maxAmount = maxAmount;
        this.payments = payments;
    }


    //SETTER METHODS

    public void setName(String name){
        this.name = name;
    }

    public void setMaxAmount(double maxAmount){
        this.maxAmount = maxAmount;
    }

    public void setPayments(ArrayList<Integer> payments) {
        this.payments = payments;
    }


    //GETTER METHODS

    public long getId(){
        return  this.id;
    }

    public String getName(){
        return this.name;
    }

    public double getMaxAmount(){
        return this.maxAmount;
    }

    public List<Integer> getPayments() {
        return this.payments;
    }

    public List<Client> getClients() {
        return this.clientLoans.stream().map(clientLoan -> clientLoan.getClient()).collect(toList());
    }

    // OTHER METHODS

    public void addClientLoan(ClientLoan clientLoan){
        clientLoan.setLoan(this);
        this.clientLoans.add(clientLoan);

    }
}
