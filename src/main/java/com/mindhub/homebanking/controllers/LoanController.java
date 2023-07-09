package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientLoanDTO;
import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanCreationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/api")
public class LoanController {

    @Autowired
    ClientService clientService;
    @Autowired
    AccountService accountService;
    @Autowired
    LoanService loanService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    ClientLoanService clientLoanService;



    @GetMapping("/loans")
    public Set<LoanDTO> getLoansList(){
        return loanService.findAll().stream().map(LoanDTO::new).collect(toSet());
    }

    @PostMapping("/loans")
    @Transactional
    public ResponseEntity<Object> createClientLoan(@RequestBody LoanApplicationDTO loanApplicationDTO, Authentication authentication){

    }

    @PostMapping("/genericLoans")
    public ResponseEntity<Object> setGenericLoan(@RequestBody LoanCreationDTO genericLoan, Authentication authentication){

        Client admin = clientService.findByEmail(authentication.getName());
        if(!admin.getEmail().equals("admin@mindhub.com") || !admin.getFirstName().equalsIgnoreCase("admin")){

            return new ResponseEntity<>("Only ADMIN can set a new loan.", HttpStatus.FORBIDDEN);
        }
        if (genericLoan.getName().isEmpty()  || genericLoan.getMaxAmount() == null || genericLoan.getPayments() == null || genericLoan.getInterestRate() == null) {
            String errorMessage = "⋆ The following fields are empty: ";
            boolean moreThanOne = false;

            if (genericLoan.getName().isEmpty()){
                errorMessage += "name";
                moreThanOne = true;
            }
            if (genericLoan.getMaxAmount() == null){
                if(moreThanOne){
                    errorMessage += ", ";
                }
                else {
                    moreThanOne = true;
                }
                errorMessage += "max amount";
            }
            if (genericLoan.getPayments() == null){
                if(moreThanOne){
                    errorMessage += ", ";
                }
                else {
                    moreThanOne = true;
                }
                errorMessage += "payments";
            }
            if (genericLoan.getInterestRate() == null){
                if(moreThanOne) {
                    errorMessage += ", ";
                }
                errorMessage += "interest rate";
            }
            errorMessage += ".";

            return new ResponseEntity<>(errorMessage, HttpStatus.FORBIDDEN);
        }
        if(genericLoan.getMaxAmount() < 10000){

            return new ResponseEntity<>("Loan must be at least of 10000 dolars", HttpStatus.FORBIDDEN);
        }
        if(genericLoan.getInterestRate() < 0 || genericLoan.getInterestRate() > 100){

            return new ResponseEntity<>("The interest rate must be between 0% and 100%", HttpStatus.FORBIDDEN);
        }
        if(genericLoan.getPayments().stream().anyMatch(payment -> payment < 0 || payment > 60)){

            return new ResponseEntity<>("Payment options must be higher than 0 and lower than 60.", HttpStatus.FORBIDDEN);
        }
        if(loanService.existsLoanByName(genericLoan.getName())){

            return new ResponseEntity<>("There cannot be two loans with the same name", HttpStatus.FORBIDDEN);
        }

        Loan loan = new Loan(genericLoan.getName(), genericLoan.getMaxAmount(), genericLoan.getPayments(), genericLoan.getInterestRate());
        loanService.save(loan);

        return new ResponseEntity<>("Loan created.", HttpStatus.OK);
    }



}
