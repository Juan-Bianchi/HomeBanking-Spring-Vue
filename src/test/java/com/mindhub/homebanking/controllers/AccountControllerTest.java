package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService accountService;
    @Mock
    private ClientService clientService;
    private AccountController underTest;

    @BeforeEach
    void arrange(){
        underTest = new AccountController(accountService, clientService);
    }

    @Test
    void shouldGetAccounts() {
        // Act
        underTest.getAccounts();

        // Assert
        verify(accountService).findAll();
    }

    @Test
    void shouldGetAccount() {
        // Arrange
        Account account = new Account();

        // Act
        given(accountService.findById(anyLong())).willReturn(account);
        underTest.getAccount(anyLong());

        // Assert
        verify(accountService).findById(anyLong());
    }

    @Test
    void shouldGetAuthenticatedUserAccounts() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        Client client = new Client();

        // Use
        given(clientService.findByEmail(authentication.getName())).willReturn(client);
        underTest.getAccounts(authentication);

        // Assert
        verify(clientService).getAccountsDTO(any(Client.class));
    }

    @Test
    void shouldGetActiveAccounts() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        Client client = new Client();

        // Act
        given(clientService.findByEmail(authentication.getName())).willReturn(client);
        Set<AccountDTO> accounts = underTest.getActiveAccounts(authentication);

        // Assert
        assertThat(accounts, notNullValue());
    }

    @Test
    void shouldCreateAccount() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        AccountDTO expectedAccountDTO = new AccountDTO();
        ResponseEntity<Object> expectedResponseEntity = new ResponseEntity<>(expectedAccountDTO, HttpStatus.CREATED);

        // Act
        given(accountService.createAccount(any(Authentication.class), eq(AccountType.CURRENT))).willReturn(expectedAccountDTO);
        ResponseEntity<Object> obtainedResponseEntity = underTest.createAccount(authentication, AccountType.CURRENT);

        // Assert
        assertThat(obtainedResponseEntity, is(expectedResponseEntity));
    }

    @Test
    void shouldCatchWhenThereIsAnExceptionCreatingAccount(){
        // Arrange
        Authentication authentication = mock(Authentication.class);
        AccountType accountType = null;

        // Act
        doThrow(new RuntimeException("Account type is not correct.")).when(accountService).createAccount(authentication, accountType);
        ResponseEntity<Object> responseEntity = underTest.createAccount(authentication, accountType);

        // Assert
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.FORBIDDEN));
        assertThat(responseEntity.getBody(), is("Account type is not correct."));
    }

    @Test
    void shouldCancelAccount() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        ResponseEntity<Object> expectedResponseEntity = new ResponseEntity<>("The account was cancelled", HttpStatus.ACCEPTED);

        // Act
        ResponseEntity<Object> obtainedResponseEntity = underTest.cancelAccount("test", authentication);

        // Assert
        assertThat(obtainedResponseEntity, is(expectedResponseEntity));
        verify(accountService).cancelAccount("test", authentication);
    }

    @Test
    void shouldCatchWhenThereIsAnExceptionCancellingAnAccount(){
        // Arrange
        Authentication authentication = mock(Authentication.class);

        // Act
        doThrow(new RuntimeException("Account number is empty. It must be provided.")).when(accountService).cancelAccount("",authentication);
        ResponseEntity<Object> responseEntity = underTest.cancelAccount("", authentication);

        // Assert
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.FORBIDDEN));
        assertThat(responseEntity.getBody(), is("Account number is empty. It must be provided."));
    }
}