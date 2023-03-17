package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Loan;

import java.util.List;

public interface LoanService {

    List<Loan> findAll();
    Loan findById(Long id);
    boolean existsLoanByName(String name);
    void save(Loan loan);
}
