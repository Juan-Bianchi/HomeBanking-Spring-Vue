package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static com.mindhub.homebanking.models.AccountType.SAVINGS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@SpringBootTest
public class RepositoriesTest {

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private TransactionRepository transactionRepository;



    @Test
    public void existLoans() {

        List<Loan> loans = loanRepository.findAll();
        assertThat(loans, is(not(empty())));
    }

    @Test
    public void existPersonalLoan() {

        List<Loan> loans = loanRepository.findAll();
        assertThat(loans, hasItem(hasProperty("name", is("Personal"))));
    }

    @Test
    public void existClients() {

        List<Client> clients = clientRepository.findAll();
        assertThat(clients, is(not(empty())));
    }

    @Test
    public void existSavingsAccount() {

        List<Client> clients = clientRepository.findAll();
        assertThat(clients, hasItem(hasProperty("accounts",(hasItem(hasProperty("type", is((SAVINGS))))) )));
    }

    @Test
    public void existAccounts() {

        List<Account> accounts = accountRepository.findAll();
        assertThat(accounts, hasSize(greaterThan(0)));
    }

    @Test
    public void existTransactions() {

        List<Account> accounts = accountRepository.findAll();
        assertThat(accounts, hasItem(hasProperty("transactions", not(emptyCollectionOf(Transaction.class)))));
    }

    @Test
    public void existCards() {

        Card card = cardRepository.findAll().get(0);
        assertThat(card, notNullValue());
    }

    @Test
    public void existCreditCard() {

        List<Card> cards = cardRepository.findAll();
        assertThat(cards, hasItem(hasProperty("type", is(CardType.CREDIT))));
    }

    @Test
    public void transactionDateIsBeforeNow() {

        List<Transaction> transactions = transactionRepository.findAll();
        LocalDateTime localDateTime = LocalDateTime.now();
        assertThat(transactions, hasItem(hasProperty("date", lessThan(localDateTime))));
    }

    @Test
    public void accountBalanceIsHigherThanZero() {

        List<Transaction> transactions = transactionRepository.findAll();
        assertThat(transactions, hasItem(hasProperty("accountBalance", greaterThan(0.0))));
    }




}