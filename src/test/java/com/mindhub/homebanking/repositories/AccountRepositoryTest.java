package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;


    @Test
    public void shouldExistsAccountByNumber() {
        //Arrange
        Account account = new Account("VIN001", LocalDateTime.now(), 50000, AccountType.SAVINGS);
        accountRepository.save(account);
        //Act
        boolean obtained = accountRepository.existsAccountByNumber(account.getNumber());
        //Assert
        assertThat(obtained, equalTo(true));
    }

    @Test
    public void shouldFindAccountByNumber() {
        //Arrange
        Account expected = new Account("VIN013", LocalDateTime.now(), 50000, AccountType.SAVINGS);
        accountRepository.save(expected);
        //Act
        Account obtained = accountRepository.findAccountByNumber(expected.getNumber());
        //Assert
        assertThat(obtained, equalTo(expected));
    }
}