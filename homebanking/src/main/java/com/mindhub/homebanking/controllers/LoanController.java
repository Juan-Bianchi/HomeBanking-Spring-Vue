package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
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
    LoanRepository loanRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    ClientLoanRepository clientLoanRepository;


    @GetMapping("/loans")
    public Set<LoanDTO> getLoansList(){

        return loanRepository.findAll().stream().map(loan -> new LoanDTO(loan)).collect(toSet());
    }

    @PostMapping("/loans")
    @Transactional
    public ResponseEntity<Object> createClientLoan(@RequestBody LoanApplicationDTO loanApplicationDTO, Authentication authentication){

        Client client = clientRepository.findByEmail(authentication.getName());
        Loan loan = loanRepository.findById(loanApplicationDTO.getIdLoan()).orElse(null);

        if (loanApplicationDTO.getIdLoan() == null  || loanApplicationDTO.getAmount() == null || loanApplicationDTO.getPayments() == null || loanApplicationDTO.getAssociatedAccountNumber().isEmpty()) {
            String errorMessage = "⋆ The following fields are empty: ";
            Boolean moreThanOne = false;

            if (loanApplicationDTO.getIdLoan() == null){
                errorMessage += "loan id";
                moreThanOne = true;
            }
            if (loanApplicationDTO.getAmount() == null){
                if(moreThanOne){
                    errorMessage += ", ";
                }
                else {
                    moreThanOne = true;
                }
                errorMessage += "amount";
            }
            if (loanApplicationDTO.getPayments() == null){
                if(moreThanOne){
                    errorMessage += ", ";
                }
                else {
                    moreThanOne = true;
                }
                errorMessage += "payments";
            }
            if (loanApplicationDTO.getAssociatedAccountNumber().isEmpty()){
                if(moreThanOne) {
                    errorMessage += ", ";
                }
                errorMessage += "account number";
            }
            errorMessage += ".";

            return new ResponseEntity<>(errorMessage, HttpStatus.FORBIDDEN);
        }
        if(loanApplicationDTO.getAmount() == 0){

            return new ResponseEntity<>("The loan amount must not be zero.", HttpStatus.FORBIDDEN);
        }
        if(loanApplicationDTO.getPayments() == 0){

            return new ResponseEntity<>("The loan payments quantity must not be zero.", HttpStatus.FORBIDDEN);
        }
        if(loan == null){

            return new ResponseEntity<>("There is not any loan with the given id.", HttpStatus.FORBIDDEN);
        }
        if(loanApplicationDTO.getAmount() < 10000){

            return new ResponseEntity<>("The loan amount must not be bigger than U$S 10.0000 .", HttpStatus.FORBIDDEN);
        }
        if(loan.getMaxAmount() < loanApplicationDTO.getAmount()){

            return  new ResponseEntity<>("The requested amount cannot exceed the current max amount for the selected loan.", HttpStatus.FORBIDDEN);
        }
        int min = loan.getPayments().stream().min(Integer::compareTo).orElse(null);
        int max = loan.getPayments().stream().max(Integer::compareTo).orElse(null);
        if(loanApplicationDTO.getPayments() < min || loanApplicationDTO.getPayments() > max){

            return new ResponseEntity<>("The payments number must among "+ min + " and " + max, HttpStatus.FORBIDDEN);
        }
        Account account = accountRepository.findAccountByNumber(loanApplicationDTO.getAssociatedAccountNumber());
        if(account == null){

            return new ResponseEntity<>("The given account does not exist.", HttpStatus.FORBIDDEN);
        }
        if(!client.getAccounts().contains(account)) {

            return new ResponseEntity<>("The given account is not related to the correct Client", HttpStatus.FORBIDDEN);
        }
        if(!loan.getPayments().contains(loanApplicationDTO.getPayments())){

            return new ResponseEntity<>("The selected payment is not among the allowed payment options.", HttpStatus.FORBIDDEN);
        }
        List<Double> previousClientLoansDuplicated = client.getLoans().stream().filter(ln-> ln.getLoan().getId() == loan.getId()).map(ln -> ln.getAmount()).collect(toList());
        if(previousClientLoansDuplicated.size() > 0){
            double previousLoanAmounts = previousClientLoansDuplicated.stream().reduce(0.0, (accumulator, amount) -> accumulator + amount);
            if(loanApplicationDTO.getAmount() +  previousLoanAmounts > loan.getMaxAmount()){

                return new ResponseEntity<>("Max amount for this type of loan cannot be exceeded. You already have previous loans of the same type. Please, be sure not to exceed the max amount.", HttpStatus.FORBIDDEN);
            }
        }


        String loanName = loan.getName();
        Transaction transaction = new Transaction(TransactionType.CREDIT, loanApplicationDTO.getAmount(), loanName + " loan approved.", LocalDateTime.now());
        account.addTransaction(transaction);
        account.setBalance(account.getBalance() + loanApplicationDTO.getAmount());
        double interestRate;
        if(loan.getId() == 2){
            interestRate = 1.24;
        }
        else if(loan.getId() == 3){
            interestRate = 1.2;
        }
        else{
            interestRate = 1.08;
        }
        ClientLoan clientLoan = new ClientLoan(client, loan, loanApplicationDTO.getAmount() * interestRate, loanApplicationDTO.getPayments());
        client.addClientLoan(clientLoan);
        loan.addClientLoan(clientLoan);

        transactionRepository.save(transaction);
        clientLoanRepository.save(clientLoan);
        accountRepository.save(account);

        return new ResponseEntity<>("Loan has been created and approved.", HttpStatus.CREATED);
    }


}
