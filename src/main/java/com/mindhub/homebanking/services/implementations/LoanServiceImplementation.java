package com.mindhub.homebanking.services.implementations;

import com.mindhub.homebanking.models.Loan;
import com.mindhub.homebanking.repositories.LoanRepository;
import com.mindhub.homebanking.services.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
