package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.TransactionDTO;
import com.mindhub.homebanking.services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    private TransactionController underTest;
    @Mock
    TransactionService transactionService;

    @BeforeEach
    void arrange() {
        underTest = new TransactionController(transactionService);
    }

    @Test
    void shouldGetTransactions() {
        // Arrange
        String accountNumber = "account number";
        LocalDateTime fromDate = LocalDateTime.now().plusDays(-5);
        LocalDateTime thruDate = LocalDateTime.now();
        List<TransactionDTO> transactionsDTO = mock(List.class);
        ResponseEntity<?> expectedResponseEntity = new ResponseEntity<>(transactionsDTO, HttpStatus.OK);

        // Act
        given(transactionService.getTransactions(accountNumber, fromDate, thruDate)).willReturn(transactionsDTO);
        ResponseEntity<?> obtainedResponseEntity = underTest.getTransactions(accountNumber, fromDate, thruDate);

        // Assert
        assertThat(expectedResponseEntity, is(obtainedResponseEntity));
        verify(transactionService).getTransactions(accountNumber, fromDate, thruDate);
    }

    @Test
    void shouldCatchWhenAnExceptionIsThrownWhileGettingTransactions() {
        // Arrange
        String accountNumber = "account number";
        LocalDateTime fromDate = LocalDateTime.now().plusDays(-5);
        LocalDateTime thruDate = LocalDateTime.now();
        ResponseEntity<?> expectedResponseEntity = new ResponseEntity<>("test", HttpStatus.FORBIDDEN);

        // Act
        doThrow(new RuntimeException("test")).when(transactionService).getTransactions(accountNumber, fromDate, thruDate);
        ResponseEntity<?> obtainedResponseEntity = underTest.getTransactions(accountNumber, fromDate, thruDate);

        // Assert
        assertThat(expectedResponseEntity, is(obtainedResponseEntity));
    }

    @Test
    void shouldPostTransactions() {
        // Arrange
        Double amount = 1000.0;
        String description = "description";
        String origAccountNumb = "origin";
        String destAccountNumb = "destination";
        Authentication authentication = mock(Authentication.class);
        ResponseEntity<Object> expectedResponseEntity = new ResponseEntity<>("The transaction has been successfully completed.", HttpStatus.OK);

        // Act
        ResponseEntity<Object> obtainedResponseEntity = underTest.postTransactions(amount, description, origAccountNumb, destAccountNumb, authentication);

        // Assert
        assertThat(expectedResponseEntity, is(obtainedResponseEntity));
        verify(transactionService).postTransactions(amount, description, origAccountNumb, destAccountNumb, authentication);
    }

    @Test
    void shouldCatchWhenAnExceptionIsThrownWhilePosting() {
        // Arrange
        Double amount = 1000.0;
        String description = "description";
        String origAccountNumb = "origin";
        String destAccountNumb = "destination";
        Authentication authentication = mock(Authentication.class);
        ResponseEntity<Object> expectedResponseEntity = new ResponseEntity<>("test", HttpStatus.FORBIDDEN);

        // Act
        doThrow(new RuntimeException("test")).when(transactionService).postTransactions(amount, description, origAccountNumb, destAccountNumb, authentication);
        ResponseEntity<Object> obtainedResponseEntity = underTest.postTransactions(amount, description, origAccountNumb, destAccountNumb, authentication);

        // Assert
        assertThat(expectedResponseEntity, is(obtainedResponseEntity));
    }
}