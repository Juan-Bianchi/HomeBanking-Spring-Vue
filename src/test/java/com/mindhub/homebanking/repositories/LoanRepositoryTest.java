package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.Loan;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
class LoanRepositoryTest {

    @Autowired
    private LoanRepository loanRepository;
    private Loan onTest;
    private List<Integer>payments;

    @BeforeEach
    public void arrange(){
        payments = new ArrayList<>();
        payments.add(6);
        payments.add(12);
        payments.add(24);
        onTest = new Loan("Simulated", 50000, payments, 1.3);
        loanRepository.save(onTest);
    }

    @AfterEach
    public void Annihilate(){
        loanRepository.delete(onTest);
        onTest = null;
        payments = null;
    }

    @Test
    void shouldExistLoanById() {
        //Act
        boolean obtained = loanRepository.existsLoanById(onTest.getId());
        //Assert
        assertThat(obtained, equalTo(true));
    }

    @Test
    void ShouldFindLoanById() {
        //Act
        Loan obtained = loanRepository.findLoanById(onTest.getId());
        //Assert
        assertThat(obtained, equalTo(onTest));
        //Annihilate
        obtained = null;
    }

    @Test
    void shouldExistsLoanByName() {
        //Act
        boolean obtained = loanRepository.existsLoanByName(onTest.getName());
        //Assert
        assertThat(obtained,equalTo(true));
    }
}