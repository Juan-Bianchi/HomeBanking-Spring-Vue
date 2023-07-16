package com.mindhub.homebanking.services.implementations;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.services.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) //turns the mocked repository into an autocloseable mock.
class AccountServiceImplementationTest {

    private AccountServiceImplementation underTest;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private ClientService clientService;

    @BeforeEach
    public void arrange(){
        underTest = new AccountServiceImplementation(accountRepository, clientService);
    }

    @Test
    public void shouldFindAllAccounts() {
        // Act
        underTest.findAll();

        // Assert
        verify(accountRepository).findAll();
    }

    @Test
    public void shouldFindAccountById() {
        // Act
        underTest.findById(anyLong());

        // Assert
        verify(accountRepository).findById(anyLong());
    }

    @Test
    public void shouldSaveAnAccount() {
        // Act
        Account account = new Account("VIN111", LocalDateTime.now(), 50000, AccountType.SAVINGS);
        underTest.save(account);
        ArgumentCaptor<Account> accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);

        // Assert
        verify(accountRepository).save(accountArgumentCaptor.capture());
        Account capturedAccount = accountArgumentCaptor.getValue();
        assertThat(capturedAccount, is(account));
    }

    @Test
    public void shouldTellIfExistsAccountByNumber() {
        // Act
        underTest.existsAccountByNumber(anyString());

        // Arrange
        verify(accountRepository).existsAccountByNumber(anyString());
    }

    @Test
    public void shouldFindAccountByNumber() {
        // Act
        underTest.findAccountByNumber(anyString());

        // Arrange
        verify(accountRepository).findAccountByNumber(anyString());
    }

    @Test
    public void shouldCreateAnAccount() {
        // Arrange / Act
        Authentication authentication = mock(Authentication.class);
        Client client = new Client();

        given(clientService.findByEmail(authentication.getName())).willReturn(client);
        underTest.createAccount(authentication, AccountType.SAVINGS);

        // Assert
        verify(accountRepository).save(ArgumentMatchers.any(Account.class));
    }

    @Test
    public void shouldThrowWhenAccountTypeIsNotOk(){
        // Arrange
        Authentication authentication = mock(Authentication.class);
        Client client = new Client();

        // Act
        given(clientService.findByEmail(authentication.getName())).willReturn(client);

        // Assert
        assertThatThrownBy(() -> underTest.createAccount(authentication, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Account type is not correct.");

        verify(accountRepository, never()).save(any());
    }

    @Test
    public void shouldThrowWhenClientAccountsMoreThan3(){
        // Arrange / Act
        Authentication authentication = mock(Authentication.class);
        Client client = new Client();
        client.addAccount(new Account("VIN011", LocalDateTime.now(), 50000, AccountType.SAVINGS));
        client.addAccount(new Account("VIN021", LocalDateTime.now(), 50000, AccountType.SAVINGS));
        client.addAccount(new Account("VIN031", LocalDateTime.now(), 50000, AccountType.SAVINGS));
        given(clientService.findByEmail(authentication.getName())).willReturn(client);

        // Assert
        assertThatThrownBy(() -> underTest.createAccount(authentication, AccountType.SAVINGS))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("The max amount of accounts have been already created");

        verify(accountRepository, never()).save(any());
    }

    @Test
    public void shouldCancelAnAccount() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        Client client = new Client();
        Account account = (new Account("VIN011", LocalDateTime.now(), 0, AccountType.SAVINGS));
        client.addAccount(account);

        // Act
        given(clientService.findByEmail(authentication.getName())).willReturn(client);
        given(accountRepository.findAccountByNumber(account.getNumber())).willReturn(account);
        underTest.cancelAccount(account.getNumber(), authentication);

        // Assert
        verify(accountRepository).save(account);
    }

    @Test
    public void shouldThrowWhenAccountNumberIsEmpty(){
        // Arrange
        Authentication authentication = mock(Authentication.class);

        //Assert
        assertThatThrownBy(() -> underTest.cancelAccount("", authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Account number is empty. It must be provided.");

        verify(accountRepository, never()).save(any());
    }

    @Test
    public void shouldThrowWhenAccountNumberDoesNotExists(){
        // Arrange
        Authentication authentication = mock(Authentication.class);
        Client client = new Client();

        //Act
        given(clientService.findByEmail(authentication.getName())).willReturn(client);

        // Assert
        assertThatThrownBy(() -> underTest.cancelAccount("pepe", authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("The account does not exist.");

        verify(accountRepository, never()).save(any());
    }

    @Test
    public void shouldThrowWhenAccountNumberIsNotCorrect(){
        // Arrange
        Authentication authentication = mock(Authentication.class);
        Client client1 = new Client();
        Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", "333");
        Account acc1 = new Account("VIN001", LocalDateTime.now(), 50000, AccountType.SAVINGS);
        cli1.addAccount(acc1);

        //Act
        given(clientService.findByEmail(authentication.getName())).willReturn(client1);
        given(accountRepository.findAccountByNumber(acc1.getNumber())).willReturn(acc1);

        // Assert
        assertThatThrownBy(() -> underTest.cancelAccount(acc1.getNumber(), authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("This account does not belong to the current user.");

        verify(accountRepository, never()).save(any());
    }

    @Test
    public void shouldThrowWhenAccountHasMoney(){
        // Arrange
        Authentication authentication = mock(Authentication.class);
        Client client1 = new Client();
        Account acc1 = new Account("VIN001", LocalDateTime.now(), 50000, AccountType.SAVINGS);
        client1.addAccount(acc1);

        //Act
        given(clientService.findByEmail(authentication.getName())).willReturn(client1);
        given(accountRepository.findAccountByNumber(acc1.getNumber())).willReturn(acc1);

        // Assert
        assertThatThrownBy(() -> underTest.cancelAccount(acc1.getNumber(), authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("You cannot delete an account if there is money in it.");

        verify(accountRepository, never()).save(any());
    }

    @Test
    public void shouldCreateAccountNumber() {
        // Act
        String number = underTest.createAccountNumber();

        // Assert
        assertThat(number, is(not(emptyOrNullString())));
    }

    @Test
    public void AccountNumberCreatedShouldNotBeDuplicated(){
        // Arrange
        String accountNumber = underTest.createAccountNumber();

        // Act
        boolean existsAccountByNumber = underTest.existsAccountByNumber(accountNumber);

        // Assert
        assertThat(existsAccountByNumber, is(false));
    }

}