package com.mindhub.homebanking.services.implementations;

import com.mindhub.homebanking.dtos.TransactionDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.repositories.TransactionRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mindhub.homebanking.models.TransactionType.CREDIT;
import static com.mindhub.homebanking.models.TransactionType.DEBIT;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplementationTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountService accountService;
    @Mock
    private ClientService clientService;
    private TransactionServiceImplementation underTest;

    @BeforeEach
    void arrange(){
        underTest = new TransactionServiceImplementation(transactionRepository, accountService, clientService);
    }

    @Test
    void shouldSaveATransaction() {
        // Arrange
        Transaction transaction = mock(Transaction.class);

        // Act
        ArgumentCaptor<Transaction> argumentCaptor = ArgumentCaptor.forClass(Transaction.class);
        underTest.save(transaction);

        // Assert
        verify(transactionRepository).save(argumentCaptor.capture());
        Transaction capturedTransaction = argumentCaptor.getValue();
        assertThat(capturedTransaction, is(transaction));
    }

    @Test
    void shouldPostTransactions() {
        // Arrange
        Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", "333");
        Account acc1 = new Account("VIN001", LocalDateTime.now(), 50000, AccountType.SAVINGS);
        Account acc2 = new Account("VIN003", LocalDateTime.now(), 10000, AccountType.SAVINGS);
        Authentication authentication = mock(Authentication.class);

        // Act
        given(clientService.findByEmail(authentication.getName())).willReturn(cli1);
        given(accountService.findAccountByNumber(acc1.getNumber())).willReturn(acc1);
        given(accountService.findAccountByNumber(acc2.getNumber())).willReturn(acc2);
        given(accountService.existsAccountByNumber(acc1.getNumber())).willReturn(true);
        given(accountService.existsAccountByNumber(acc2.getNumber())).willReturn(true);
        underTest.postTransactions(2000.0, "test", acc1.getNumber(), acc2.getNumber(), authentication);


        // Assert
        verify(transactionRepository, times(2)).save(any(Transaction.class));
        verify(accountService, times(2)).save(any(Account.class));
    }

    @Test
    void shouldThrowWhenClientEmailIsNotCorrect(){
        // Arrange
        Account acc1 = new Account("VIN001", LocalDateTime.now(), 50000, AccountType.SAVINGS);
        Authentication authentication = mock(Authentication.class);

        // Act
        // Assert
        assertThatThrownBy(()-> underTest.postTransactions(2000.0, "test", acc1.getNumber(), acc1.getNumber(), authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("The origin account does not belong to the authenticated client.");

        verify(transactionRepository, never()).save(any());
        verify(accountService, never()).save(any());
    }

    @Test
    void shouldThrowWhenThereIsANullField(){
        // Arrange
        Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", "333");;
        Authentication authentication = mock(Authentication.class);

        // Act
        given(clientService.findByEmail(authentication.getName())).willReturn(cli1);

        // Assert
        assertThatThrownBy(()-> underTest.postTransactions(null, "", "", "", authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("⋆ Please, fill the following fields: amount, description, origin account number, destination account number.");

        verify(transactionRepository, never()).save(any());
        verify(accountService, never()).save(any());
    }

    @Test
    void shouldThrowWhenOriginAccountIsNotActive(){
        // Arrange
        Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", "333");
        Account acc1 = new Account("VIN001", LocalDateTime.now(), 50000, AccountType.SAVINGS);
        Account acc2 = new Account("VIN003", LocalDateTime.now(), 10000, AccountType.SAVINGS);

        cli1.addAccount(acc1);
        acc1.setIsActive(false);
        Authentication authentication = mock(Authentication.class);

        // Act
        given(clientService.findByEmail(authentication.getName())).willReturn(cli1);
        given(accountService.findAccountByNumber(acc1.getNumber())).willReturn(acc1);
        given(accountService.existsAccountByNumber(acc1.getNumber())).willReturn(true);
        given(accountService.existsAccountByNumber(acc2.getNumber())).willReturn(true);

        // Assert
        assertThatThrownBy(()-> underTest.postTransactions(2000.0, "test", acc1.getNumber(), acc2.getNumber(), authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("The origin account is not active, therefore not transactions can be made.");

        verify(transactionRepository, never()).save(any());
        verify(accountService, never()).save(any());
    }

    @Test
    void shouldThrowWhenBalanceIsInsufficient(){
        // Arrange
        Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", "333");
        Account acc1 = new Account("VIN001", LocalDateTime.now(), 1000, AccountType.SAVINGS);
        Account acc2 = new Account("VIN002", LocalDateTime.now(), 10000, AccountType.SAVINGS);
        cli1.addAccount(acc1);
        Authentication authentication = mock(Authentication.class);

        // Act
        given(clientService.findByEmail(authentication.getName())).willReturn(cli1);
        given(accountService.findAccountByNumber(acc1.getNumber())).willReturn(acc1).willReturn(acc2);
        given(accountService.existsAccountByNumber(acc1.getNumber())).willReturn(true);
        given(accountService.existsAccountByNumber(acc2.getNumber())).willReturn(true);

        // Assert
        assertThatThrownBy(()-> underTest.postTransactions(2000.0, "test", acc1.getNumber(), acc2.getNumber(), authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("The available balance is insufficient to complete the transaction.");

        verify(transactionRepository, never()).save(any());
        verify(accountService, never()).save(any());
    }

    @Test
    void shouldThrowWhenDestinationAccountNumberIsWrong(){
        // Arrange
        Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", "333");
        Account acc1 = new Account("VIN001", LocalDateTime.now(), 10000, AccountType.SAVINGS);
        Account acc2 = new Account("VIN003", LocalDateTime.now(), 10000, AccountType.SAVINGS);
        cli1.addAccount(acc1);
        acc2.setIsActive(false);
        Authentication authentication = mock(Authentication.class);

        // Act
        given(clientService.findByEmail(authentication.getName())).willReturn(cli1);
        given(accountService.findAccountByNumber(acc1.getNumber())).willReturn(acc1);
        given(accountService.findAccountByNumber(acc2.getNumber())).willReturn(acc2);
        given(accountService.existsAccountByNumber(acc1.getNumber())).willReturn(true);
        given(accountService.existsAccountByNumber(acc2.getNumber())).willReturn(true);

        // Assert
        assertThatThrownBy(()-> underTest.postTransactions(2000.0, "test", acc1.getNumber(), acc2.getNumber(), authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("The destination account is not active. Therefore it cannot receive money.");

        verify(transactionRepository, never()).save(any());
        verify(accountService, never()).save(any());
    }

    @Test
    void shouldThrowWhenDestinationAccountNumberIsTheSameInOriginAccount(){
        // Arrange
        Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", "333");
        Account acc1 = new Account("VIN001", LocalDateTime.now(), 10000, AccountType.SAVINGS);
        Account acc2 = new Account("VIN001", LocalDateTime.now(), 10000, AccountType.SAVINGS);
        cli1.addAccount(acc1);
        Authentication authentication = mock(Authentication.class);

        // Act
        given(clientService.findByEmail(authentication.getName())).willReturn(cli1);

        // Assert
        assertThatThrownBy(()-> underTest.postTransactions(2000.0, "test", acc1.getNumber(), acc2.getNumber(), authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("The origin account number must be different to the destination account number.");

        verify(transactionRepository, never()).save(any());
        verify(accountService, never()).save(any());
    }

    @Test
    void shouldThrowWhenOriginAccountDoesNotExist(){
        // Arrange
        Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", "333");
        Account acc1 = new Account("VIN001", LocalDateTime.now(), 10000, AccountType.SAVINGS);
        Account acc2 = new Account("VIN002", LocalDateTime.now(), 10000, AccountType.SAVINGS);
        cli1.addAccount(acc1);
        Authentication authentication = mock(Authentication.class);

        // Act
        given(clientService.findByEmail(authentication.getName())).willReturn(cli1);

        // Assert
        assertThatThrownBy(()-> underTest.postTransactions(2000.0, "test", acc1.getNumber(), acc2.getNumber(), authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("The origin account number does not exist.");

        verify(transactionRepository, never()).save(any());
        verify(accountService, never()).save(any());
    }

    @Test
    void shouldThrowWhenDestinationAccountDoesNotExist(){
        // Arrange
        Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", "333");
        Account acc1 = new Account("VIN001", LocalDateTime.now(), 10000, AccountType.SAVINGS);
        Account acc2 = new Account("VIN002", LocalDateTime.now(), 10000, AccountType.SAVINGS);
        cli1.addAccount(acc1);
        Authentication authentication = mock(Authentication.class);

        // Act
        given(clientService.findByEmail(authentication.getName())).willReturn(cli1);
        given(accountService.existsAccountByNumber(acc1.getNumber())).willReturn(true);

        // Assert
        assertThatThrownBy(()-> underTest.postTransactions(2000.0, "test", acc1.getNumber(), acc2.getNumber(), authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("The destination account number does not exist.");

        verify(transactionRepository, never()).save(any());
        verify(accountService, never()).save(any());
    }

    @Test
    void shouldThrowWhenTransferAmountIsLowerThanMinimum(){
        // Arrange
        Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", "333");
        Account acc1 = new Account("VIN001", LocalDateTime.now(), 10000, AccountType.SAVINGS);
        Account acc2 = new Account("VIN002", LocalDateTime.now(), 10000, AccountType.SAVINGS);
        cli1.addAccount(acc1);
        Authentication authentication = mock(Authentication.class);

        // Act
        given(clientService.findByEmail(authentication.getName())).willReturn(cli1);
        given(accountService.existsAccountByNumber(acc1.getNumber())).willReturn(true);
        given(accountService.existsAccountByNumber(acc2.getNumber())).willReturn(true);

        // Assert
        assertThatThrownBy(()-> underTest.postTransactions(0.0, "test", acc1.getNumber(), acc2.getNumber(), authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("The minimum transfer amount is one dollar.");

        verify(transactionRepository, never()).save(any());
        verify(accountService, never()).save(any());
    }

    @Test
    void shouldGetAllTransactions() {
        // Arrange
        Account acc1 = new Account("VIN001", LocalDateTime.now(), 10000, AccountType.SAVINGS);
        Transaction tr0 = new Transaction(CREDIT, 800, "Transfer", LocalDateTime.now().plusDays(-4));
        Transaction tr1 = new Transaction(CREDIT, 300, "Transfer", LocalDateTime.now());
        Transaction tr2 = new Transaction(DEBIT, -800, "Transfer", LocalDateTime.now().plusDays(4));
        TransactionDTO tr3 = new TransactionDTO(tr0);
        TransactionDTO tr4 = new TransactionDTO(tr1);
        TransactionDTO tr5 = new TransactionDTO(tr2);

        // Act
        acc1.addTransaction(tr0);
        acc1.addTransaction(tr1);
        acc1.addTransaction(tr2);
        given(accountService.findAccountByNumber(acc1.getNumber())).willReturn(acc1);
        List<TransactionDTO> obtained = underTest.getTransactions(acc1.getNumber(), null, null);

        // Assert
        assertThat(obtained, containsInAnyOrder(equalTo(tr3), equalTo(tr4), equalTo(tr5)));
    }

    @Test
    void shouldGetFromDateTransactions() {
        // Arrange
        Account acc1 = new Account("VIN001", LocalDateTime.now(), 10000, AccountType.SAVINGS);
        Transaction tr0 = new Transaction(CREDIT, 800, "Transfer", LocalDateTime.now().plusDays(-4));
        Transaction tr1 = new Transaction(CREDIT, 300, "Transfer", LocalDateTime.now());
        Transaction tr2 = new Transaction(DEBIT, -800, "Transfer", LocalDateTime.now().plusDays(4));
        TransactionDTO tr4 = new TransactionDTO(tr1);
        TransactionDTO tr5 = new TransactionDTO(tr2);

        // Act
        acc1.addTransaction(tr0);
        acc1.addTransaction(tr1);
        acc1.addTransaction(tr2);
        given(accountService.findAccountByNumber(acc1.getNumber())).willReturn(acc1);
        List<TransactionDTO> obtained = underTest.getTransactions(acc1.getNumber(), LocalDateTime.now().plusHours(-1), null);

        // Assert
        assertThat(obtained, containsInAnyOrder(equalTo(tr4), equalTo(tr5)));
    }

    @Test
    void shouldGetThruDateTransactions() {
        // Arrange
        Account acc1 = new Account("VIN001", LocalDateTime.now(), 10000, AccountType.SAVINGS);
        Transaction tr0 = new Transaction(CREDIT, 800, "Transfer", LocalDateTime.now().plusDays(-4));
        Transaction tr1 = new Transaction(CREDIT, 300, "Transfer", LocalDateTime.now());
        Transaction tr2 = new Transaction(DEBIT, -800, "Transfer", LocalDateTime.now().plusDays(4));
        TransactionDTO tr3 = new TransactionDTO(tr0);
        TransactionDTO tr4 = new TransactionDTO(tr1);

        // Act
        acc1.addTransaction(tr0);
        acc1.addTransaction(tr1);
        acc1.addTransaction(tr2);
        given(accountService.findAccountByNumber(acc1.getNumber())).willReturn(acc1);
        List<TransactionDTO> obtained = underTest.getTransactions(acc1.getNumber(), null, LocalDateTime.now());

        // Assert
        assertThat(obtained, containsInAnyOrder(equalTo(tr3), equalTo(tr4)));
    }

    @Test
    void shouldThrowWhenAccountNotExist(){
        // Act
        // Assert
        assertThatThrownBy(()-> underTest.getTransactions("test", null, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("The account does not exist.");
    }
}