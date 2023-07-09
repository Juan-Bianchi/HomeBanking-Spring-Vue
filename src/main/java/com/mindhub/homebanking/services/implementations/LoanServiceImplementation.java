package com.mindhub.homebanking.services.implementations;

import com.mindhub.homebanking.dtos.ClientLoanDTO;
import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanCreationDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.LoanRepository;
import com.mindhub.homebanking.services.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class LoanServiceImplementation implements LoanService {

    @Autowired
    LoanRepository loanRepository;


    @Override
    public List<Loan> findAll() {
        return loanRepository.findAll();
    }

    @Override
    public Loan findById(Long id) {
        return loanRepository.findById(id).orElse(null);
    }

    @Override
    public boolean existsLoanByName(String name) {
        return loanRepository.existsLoanByName(name);
    }

    @Override
    public void save(Loan loan) {
        loanRepository.save(loan);
    }

    @Override
    public ResponseEntity<Object> createClientLoan(LoanApplicationDTO loanApplicationDTO, Authentication authentication) {
        Client client = clientService.findByEmail(authentication.getName());
        Loan loan = loanService.findById(loanApplicationDTO.getIdLoan());

        if (loanApplicationDTO.getIdLoan() == null  || loanApplicationDTO.getAmount() == null || loanApplicationDTO.getPayments() == null || loanApplicationDTO.getAssociatedAccountNumber().isEmpty()) {
            String errorMessage = "⋆ The following fields are empty: ";
            boolean moreThanOne = false;

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
        Account account = accountService.findAccountByNumber(loanApplicationDTO.getAssociatedAccountNumber());
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
        double finalLoanAmount = loanApplicationDTO.getAmount() + loanApplicationDTO.getAmount() * loan.getInterestRate() / 100;
        ClientLoan clientLoan = new ClientLoan(client, loan, finalLoanAmount, loanApplicationDTO.getPayments());
        client.addClientLoan(clientLoan);
        loan.addClientLoan(clientLoan);

        transactionService.save(transaction);
        clientLoanService.save(clientLoan);
        accountService.save(account);

        ClientLoanDTO clientLoanDTO = new ClientLoanDTO(clientLoan);

        return new ResponseEntity<>(clientLoanDTO, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Object> setGenericLoan(LoanCreationDTO genericLoan, Authentication authentication) {
        return null;
    }
}
