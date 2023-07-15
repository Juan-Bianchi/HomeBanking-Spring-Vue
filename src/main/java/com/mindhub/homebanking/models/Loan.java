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
    private Double maxAmount;
    @ElementCollection
    @Column(name="payments")
    private List<Integer> payments = new ArrayList<>();
    @OneToMany(mappedBy = "loan", fetch = FetchType.EAGER)
    private Set<ClientLoan> clientLoans = new HashSet<>();
    private Double interestRate;


    //CONSTRUCTORS

    public Loan(){};

    public Loan(String name, Double maxAmount, List<Integer> payments, Double interestRate){
        this.name = name;
        this.maxAmount = maxAmount;
        this.payments = payments;
        this.interestRate = interestRate;
    }


    // OTHER METHODS

    public void addClientLoan(ClientLoan clientLoan){
        clientLoan.setLoan(this);
        this.clientLoans.add(clientLoan);

    }

    @Override
    public String toString(){
        return "Loan: { \n " +
                        "id: " + this.id + ",\n" +
                        "name: " + this.name + ",\n" +
                        "maxAmount: " + this.maxAmount + ",\n" +
                        "payments: " + this.payments + ",\n" +
                        "clientLoans: " + this.clientLoans + ",\n" +
                        "}";
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

    public void setInterestRate(Double interestRate){
        this.interestRate = interestRate;
    }


    //GETTER METHODS

    public long getId(){
        return  this.id;
    }

    public String getName(){
        return this.name;
    }

    public Double getMaxAmount(){
        return this.maxAmount;
    }

    public List<Integer> getPayments() {
        return this.payments;
    }

    public List<Client> getClients() {
        return this.clientLoans.stream().map(clientLoan -> clientLoan.getClient()).collect(toList());
    }

    public Double getInterestRate(){
        return this.interestRate;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Loan other = (Loan) obj;
        return this.id == other.id;
    }
}
