package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanCreationDTO;
import com.mindhub.homebanking.models.Loan;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface LoanService {

    List<Loan> findAll();
    Loan findById(Long id);
    boolean existsLoanByName(String name);
    void save(Loan loan);
    ResponseEntity<Object> createClientLoan(@RequestBody LoanApplicationDTO loanApplicationDTO, Authentication authentication);
    ResponseEntity<Object> setGenericLoan(@RequestBody LoanCreationDTO genericLoan, Authentication authentication);


}
