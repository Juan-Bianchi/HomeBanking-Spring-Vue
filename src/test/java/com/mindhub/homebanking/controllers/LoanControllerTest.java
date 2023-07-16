package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientLoanDTO;
import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanCreationDTO;
import com.mindhub.homebanking.services.LoanService;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanControllerTest {

    private LoanController underTest;
    @Mock
    LoanService loanService;

    @BeforeEach
    void arrange() {
        underTest = new LoanController(loanService);
    }

    @Test
    void shouldGetLoansList() {
        // Act
        underTest.getLoansList();

        // Assert
        verify(loanService).findAll();
    }

    @Test
    void shouldCreateClientLoan() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        LoanApplicationDTO loanApplicationDTO = new LoanApplicationDTO();
        ClientLoanDTO clientLoanDTO = mock(ClientLoanDTO.class);
        ResponseEntity<Object> expectedResponseEntity = new ResponseEntity<>(clientLoanDTO, HttpStatus.CREATED);

        // Act
        given(loanService.createClientLoan(loanApplicationDTO, authentication)).willReturn(clientLoanDTO);
        ResponseEntity<Object> obtainedResponseEntity = underTest.createClientLoan(loanApplicationDTO, authentication);

        // Assert
        assertThat(expectedResponseEntity, is(obtainedResponseEntity));
        verify(loanService).createClientLoan(loanApplicationDTO, authentication);
    }

    @Test
    void shouldCatchWhenAnExceptionIsThrownWhileCreating() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        LoanApplicationDTO loanApplicationDTO = new LoanApplicationDTO();
        ResponseEntity<Object> expectedResponseEntity = new ResponseEntity<>("test", HttpStatus.FORBIDDEN);

        // Act
        doThrow(new RuntimeException("test")).when(loanService).createClientLoan(loanApplicationDTO, authentication);
        ResponseEntity<Object> obatinedResponseEntity = underTest.createClientLoan(loanApplicationDTO, authentication);

        // Assert
        assertThat(expectedResponseEntity, is(obatinedResponseEntity));
    }

    @Test
    void shouldSetGenericLoan() {
        // Arrange
        LoanCreationDTO genericLoan = new LoanCreationDTO();
        Authentication authentication = mock(Authentication.class);
        ResponseEntity<Object> expectedResponseEntity = new ResponseEntity<>("Loan created.", HttpStatus.OK);

        // Act
        ResponseEntity<Object> obatinedResponseEntity = underTest.setGenericLoan(genericLoan, authentication);

        // Assert
        assertThat(expectedResponseEntity, is(obatinedResponseEntity));
    }

    @Test
    void shouldCatchWhenAnExceptionIsThrownWhileSetting() {
        // Arrange
        LoanCreationDTO genericLoan = new LoanCreationDTO();
        Authentication authentication = mock(Authentication.class);
        ResponseEntity<Object> expectedResponseEntity = new ResponseEntity<>("test", HttpStatus.FORBIDDEN);

        // Act
        doThrow(new RuntimeException("test")).when(loanService).setGenericLoan(genericLoan, authentication);
        ResponseEntity<Object> obtainedResponseEntity = underTest.setGenericLoan(genericLoan, authentication);

        // Assert
        assertThat(expectedResponseEntity, is(obtainedResponseEntity));
    }
}