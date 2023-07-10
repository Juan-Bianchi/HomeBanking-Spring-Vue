package com.mindhub.homebanking.services.implementations;

import com.mindhub.homebanking.dtos.ClientLoanDTO;
import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanCreationDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.LoanRepository;
import com.mindhub.homebanking.services.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class LoanServiceImplementation implements LoanService {

    private final LoanRepository loanRepository;
    private final ClientService clientService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final ClientLoanService clientLoanService;


    public LoanServiceImplementation (LoanRepository loanRepository, ClientService clientService, AccountService accountService, TransactionService transactionService, ClientLoanService clientLoanService){
        this.clientService = clientService;
        this.loanRepository = loanRepository;
        this.accountService = accountService;
        this.clientLoanService = clientLoanService;
        this.transactionService = transactionService;
    }
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
    public ClientLoanDTO createClientLoan(LoanApplicationDTO loanApplicationDTO, Authentication authentication) {
        Client client = clientService.findByEmail(authentication.getName());
        Loan loan = this.findById(loanApplicationDTO.getIdLoan());

        if (thereIsANullField(loanApplicationDTO)) {
            String errorMessage = verifyNullFields(loanApplicationDTO);
            throw new RuntimeException(errorMessage);
        }
        if(loan == null){
            throw new RuntimeException("There is not any loan with the given id.");
        }
        if(someFieldIsNotValid(loanApplicationDTO, loan)){
            verifyNotValidFields(loanApplicationDTO, loan);
        }

        Account account = accountService.findAccountByNumber(loanApplicationDTO.getAssociatedAccountNumber());
        if(account == null){
            throw new RuntimeException("The given account does not exist.");
        }
        if(!client.getAccounts().contains(account)) {
            throw new RuntimeException("The given account is not related to the correct Client");
        }
        List<Double> previousClientLoansDuplicated = client.getLoans().stream().filter(ln-> ln.getLoan().getId() == loan.getId()).map(ln -> ln.getAmount()).collect(toList());
        if(previousClientLoansDuplicated.size() > 0){
            double previousLoanAmounts = previousClientLoansDuplicated.stream().reduce(0.0, (accumulator, amount) -> accumulator + amount);
            if(loanApplicationDTO.getAmount() +  previousLoanAmounts > loan.getMaxAmount()){
                throw new RuntimeException("Max amount for this type of loan cannot be exceeded. You already have previous loans of the same type. Please, be sure not to exceed the max amount.");
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

        return new ClientLoanDTO(clientLoan);
    }

    @Override
    public void setGenericLoan(LoanCreationDTO genericLoan, Authentication authentication) {
        Client admin = clientService.findByEmail(authentication.getName());
        if(!admin.getEmail().equals("admin@mindhub.com") || !admin.getFirstName().equalsIgnoreCase("admin")){
            throw new RuntimeException("Only ADMIN can set a new loan.");
        }
        if (thereIsANullField(genericLoan)) {
            String errorMessage = verifyNullFields(genericLoan);
            throw new RuntimeException(errorMessage);
        }
        if(someFiledIsNotValid(genericLoan)) {
            verifyNotValidFields(genericLoan);
        }

        Loan loan = new Loan(genericLoan.getName(), genericLoan.getMaxAmount(), genericLoan.getPayments(), genericLoan.getInterestRate());
        this.save(loan);
    }


    // AUXILIARY METHODS
    private String verifyNullFields(LoanApplicationDTO loanApplicationDTO){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("⋆ The following fields are empty: ");
        boolean moreThanOne = false;

        if (loanApplicationDTO.getIdLoan() == null){
            stringBuilder.append("loan id");
            moreThanOne = true;
        }
        if (loanApplicationDTO.getAmount() == null){
            if(moreThanOne){
                stringBuilder.append(", ");
            }
            else {
                moreThanOne = true;
            }
            stringBuilder.append("amount");
        }
        if (loanApplicationDTO.getPayments() == null){
            if(moreThanOne){
                stringBuilder.append(", ");
            }
            else {
                moreThanOne = true;
            }
            stringBuilder.append("payments");
        }
        if (loanApplicationDTO.getAssociatedAccountNumber().isEmpty()){
            if(moreThanOne) {
                stringBuilder.append(", ");
            }
            stringBuilder.append("account number");
        }
        stringBuilder.append(".");

        return  stringBuilder.toString();
    }

    private boolean thereIsANullField(LoanApplicationDTO loanApplicationDTO){
        return loanApplicationDTO.getIdLoan() == null  || loanApplicationDTO.getAmount() == null || loanApplicationDTO.getPayments() == null || loanApplicationDTO.getAssociatedAccountNumber().isEmpty();
    }

    private boolean thereIsANullField(LoanCreationDTO genericLoan){
        return genericLoan.getName().isEmpty()  || genericLoan.getMaxAmount() == null || genericLoan.getPayments() == null || genericLoan.getInterestRate() == null;
    }

    private boolean someFieldIsNotValid(LoanApplicationDTO loanApplicationDTO, Loan loan){
        Integer min = loan.getPayments().stream().min(Integer::compareTo).orElse(null);
        Integer max = loan.getPayments().stream().max(Integer::compareTo).orElse(null);
        boolean notValid = min == null || max == null;
        if(!notValid){
            notValid = loanApplicationDTO.getPayments() < min || loanApplicationDTO.getPayments() > max || loanApplicationDTO.getAmount() < 10000;
        }

        return notValid;
    }

    private void verifyNotValidFields(LoanApplicationDTO loanApplicationDTO, Loan loan){
        if(loanApplicationDTO.getAmount() < 10000){
            throw new RuntimeException("The loan amount must not be lower than U$S 10.0000 .");
        }
        if(loan.getMaxAmount() < loanApplicationDTO.getAmount()){
            throw new RuntimeException("The requested amount cannot exceed the current max amount for the selected loan.");
        }
        Integer min = loan.getPayments().stream().min(Integer::compareTo).orElse(null);
        Integer max = loan.getPayments().stream().max(Integer::compareTo).orElse(null);
        boolean notValid = min == null || max == null;
        if(notValid || loanApplicationDTO.getPayments() < min || loanApplicationDTO.getPayments() > max){
            throw new RuntimeException("The payments number must among "+ min + " and " + max);
        }
        if(!loan.getPayments().contains(loanApplicationDTO.getPayments())){
            throw new RuntimeException("The selected payment is not among the allowed payment options.");
        }
    }

    private String verifyNullFields(LoanCreationDTO genericLoan){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("⋆ The following fields are empty: ");
        boolean moreThanOne = false;

        if (genericLoan.getName().isEmpty()){
            stringBuilder.append("name");
            moreThanOne = true;
        }
        if (genericLoan.getMaxAmount() == null){
            if(moreThanOne){
                stringBuilder.append(", ");
            }
            else {
                moreThanOne = true;
            }
            stringBuilder.append("max amount");
        }
        if (genericLoan.getPayments() == null){
            if(moreThanOne){
                stringBuilder.append(", ");
            }
            else {
                moreThanOne = true;
            }
            stringBuilder.append("payments");
        }
        if (genericLoan.getInterestRate() == null){
            if(moreThanOne) {
                stringBuilder.append(", ");
            }
            stringBuilder.append("interest rate");
        }
        stringBuilder.append(".");

        return  stringBuilder.toString();
    }

    private boolean someFiledIsNotValid(LoanCreationDTO genericLoan){
        return genericLoan.getMaxAmount() < 10000 || genericLoan.getInterestRate() < 0 || genericLoan.getInterestRate() > 100 || genericLoan.getPayments().stream().anyMatch(payment -> payment < 0 || payment > 60) || this.existsLoanByName(genericLoan.getName());
    }

    private void verifyNotValidFields(LoanCreationDTO genericLoan) {
        if(genericLoan.getMaxAmount() < 10000){
            throw new RuntimeException("Loan must be at least of 10000 dolars");
        }
        if(genericLoan.getInterestRate() < 0 || genericLoan.getInterestRate() > 100){
            throw new RuntimeException("The interest rate must be between 0% and 100%");
        }
        if(genericLoan.getPayments().stream().anyMatch(payment -> payment < 0 || payment > 60)){
            throw new RuntimeException("Payment options must be higher than 0 and lower than 60.");
        }
        if(this.existsLoanByName(genericLoan.getName())){
            throw new RuntimeException("There cannot be two loans with the same name");
        }
    }
}
