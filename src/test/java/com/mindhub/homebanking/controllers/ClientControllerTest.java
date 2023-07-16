package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

    private ClientController underTest;
    @Mock
    private ClientService clientService;

    @BeforeEach
    void arrange(){
        underTest = new ClientController(clientService);
    }

    @Test
    void shouldGetClients() {
        // Act
        underTest.getClients();

        // Assert
        verify(clientService).findAll();
    }

    @Test
    void shouldGetClient() {
        // Arrange
        Client client = new Client();

        // Act
        given(clientService.findById(anyLong())).willReturn(client);
        ClientDTO obtainedClientDTO = underTest.getClient(anyLong());

        // Assert
        verify(clientService).findById(anyLong());
        assertThat(obtainedClientDTO, isA(ClientDTO.class));
    }

    @Test
    void shouldGetCurrentClient() {
        // Arrange
        Client client = new Client();
        Authentication authentication = mock(Authentication.class);

        // Act
        given(clientService.findByEmail(authentication.getName())).willReturn(client);
        ClientDTO obtainedClientDTO = underTest.getCurrentClient(authentication);

        // Assert
        verify(clientService).findByEmail(authentication.getName());
        assertThat(obtainedClientDTO, isA(ClientDTO.class));
    }

    @Test
    void shouldRegisterAClient() {
        // Arrange
        String firstName = "test";
        String lastName = "test";
        String email = "test";
        String password = "test";
        ResponseEntity<Object> expectedResponseEntity = new ResponseEntity<>(HttpStatus.CREATED);

        // Act
        ResponseEntity<Object> obtainedResponseEntity = underTest.register(firstName, lastName, email, password);

        // Assert
        assertThat(expectedResponseEntity, is(obtainedResponseEntity));
        verify(clientService).register(firstName, lastName, email, password);
    }

    @Test
    void shouldCatchWhenAnExceptionIsThrownWhileRegistering() {
        // Arrange
        String firstName = "test";
        String lastName = "";
        String email = "test";
        String password = "test";
        ResponseEntity<Object> expectedResponseEntity = new ResponseEntity<>("test", HttpStatus.FORBIDDEN);

        // Act
        doThrow(new RuntimeException("test")).when(clientService).register(firstName, lastName, email, password);
        ResponseEntity<Object> obtainedResponseEntity = underTest.register(firstName, lastName, email, password);

        // Assert
        assertThat(expectedResponseEntity, is(obtainedResponseEntity));
    }

    @Test
    void shouldUpdateLastLogin() {
        // Arrange
        String email = "email";
        String newLoginDate = "date";
        String lastLoginDate = "date";
        ResponseEntity<?> expectedResponseEntity = new ResponseEntity<>(HttpStatus.ACCEPTED);

        // Act
        ResponseEntity<?> obtainedResponseEntity = underTest.updateLastLogin(email, newLoginDate, lastLoginDate);

        // Assert
        assertThat(expectedResponseEntity, is(obtainedResponseEntity));
    }

    @Test
    void shouldCatchWhenAnExceptionIsThrownWhileUpdating() {
        // Arrange
        String email = "";
        String newLoginDate = "date";
        String lastLoginDate = "date";
        ResponseEntity<?> expectedResponseEntity = new ResponseEntity<>("test", HttpStatus.FORBIDDEN);

        // Act
        doThrow(new RuntimeException("test")).when(clientService).updateLastLogin(email, newLoginDate, lastLoginDate);
        ResponseEntity<?> obtainedResponseEntity = underTest.updateLastLogin(email, newLoginDate, lastLoginDate);

        // Assert
        assertThat(expectedResponseEntity, is(obtainedResponseEntity));
    }
}