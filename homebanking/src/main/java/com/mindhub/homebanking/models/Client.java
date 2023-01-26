package com.mindhub.homebanking.models;


import org.hibernate.annotations.GenericGenerator;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;



@Entity
public class Client {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String firstName;
    private String lastName;
    private String email;

    //CONSTRUCTORS
    public  Client(){}

    public Client(String first, String last, String email) {
        this.firstName = first;
        this.lastName = last;
        this.email = email;
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

    //SPECIFIC METHODS
    @Override
    public String toString(){
        return
            "firsName: " + this.firstName + ",\n" +
            "lastName: " + this.lastName + ",\n" +
            "email: " + this.email + ",\n" +
            "id: " + this.id;
        }
}

