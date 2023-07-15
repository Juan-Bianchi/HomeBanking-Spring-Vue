package com.mindhub.homebanking.services.implementations;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ClientServiceImplementationTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private AccountService accountService;
    @Mock
    private PasswordEncoder passwordEncoder;
    private ClientServiceImplementation underTest;

    @BeforeEach
    void arrange(){
        underTest = new ClientServiceImplementation(clientRepository, accountService, passwordEncoder);
    }

    @Test
    void shouldFindAllClients() {
        // Act
        underTest.findAll();

        // Assert
        verify(clientRepository).findAll();
    }

    @Test
    void shouldFindClientById() {
        // Act
        underTest.findById(anyLong());

        // Assert
        verify(clientRepository).findById(anyLong());
    }

    @Test
    void shouldFindClientByEmail() {
        // Act
        underTest.findByEmail(anyString());

        // Assert
        verify(clientRepository).findByEmail(anyString());
    }

    @Test
    void shouldSaveClient() {
        // Act
        Client client = mock(Client.class);
        underTest.save(client);
        ArgumentCaptor<Client> argumentCaptor = ArgumentCaptor.forClass(Client.class);

        // Assert
        verify(clientRepository).save(argumentCaptor.capture());
        Client capturedClient = argumentCaptor.getValue();
        assertThat(capturedClient, is(client));
    }

    @Test
    void shouldGetAccountsDTO() {
        // Arrange
        Account acc1 = new Account("VIN001", LocalDateTime.now(), 50000, AccountType.SAVINGS);
        Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", "333");

        // Act
        cli1.addAccount(acc1);
        Set<AccountDTO> obtained = underTest.getAccountsDTO(cli1);

        // Assert
        assertThat(obtained, is(not(empty())));

        // Annihilate
        acc1 = null;
        cli1 = null;
        obtained = null;
    }

    @Test
    void shouldGetActiveAccountsDTO() {
        // Arrange
        Account acc1 = new Account("VIN001", LocalDateTime.now(), 50000, AccountType.SAVINGS);
        Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", "333");

        // Act
        cli1.addAccount(acc1);
        Set<AccountDTO> obtained = underTest.getActiveAccountsDTO(cli1);

        // Assert
        assertThat(obtained, is(not(empty())));

        // Annihilate
        acc1 = null;
        cli1 = null;
        obtained = null;
    }

    @Test
    void shouldRegisterClient() {
        // Arrange
        Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", "333");

        // Act
        underTest.register(cli1.getFirstName(), cli1.getLastName(), cli1.getEmail(), cli1.getPassword());

        // Assert
        verify(clientRepository).save(ArgumentMatchers.any());
        verify(accountService).save(ArgumentMatchers.any());
    }

    @Test
    void shouldThrowWhenThereIsANullField(){
        // Act
        // Assert
        assertThatThrownBy(()-> underTest.register("", "", "", ""))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("⋆ Please, fill the following fields: first names, last names, email, password.");
        verify(clientRepository, never()).save(any());
        verify(accountService, never()).save(any());
    }

    @Test
    void shouldThrowWhenEmailIsInUse(){
        // Arrange
        Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", "333");

        // Act
        given(underTest.findByEmail(cli1.getEmail())).willReturn(cli1);

        // Assert
        assertThatThrownBy(()-> underTest.register(cli1.getFirstName(), cli1.getLastName(), cli1.getEmail(), cli1.getPassword()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("⋆ Email already in use");
        verify(clientRepository, never()).save(any());
        verify(accountService, never()).save(any());
    }

    @Test
    void shouldUpdateLastLogin() {
        // Arrange
        Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", "333");
        
        // Act
        given(underTest.findByEmail(cli1.getEmail())).willReturn(cli1);
        underTest.updateLastLogin(cli1.getEmail(), cli1.getLastLogin(), cli1.getNewLogin());
        
        // Assert
        verify(clientRepository).save(cli1);
    }

    @Test
    void shouldThrowWhenEmailIsWrong(){
        // Arrange
        Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", "333");

        // Act
        // Assert
        assertThatThrownBy(()-> underTest.updateLastLogin(cli1.getEmail(), cli1.getLastLogin(), cli1.getNewLogin()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Client does not exist.");

        verify(clientRepository, never()).save(any());
    }
}