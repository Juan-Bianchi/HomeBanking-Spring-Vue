package com.mindhub.homebanking.services.implementations;


import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanCreationDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.LoanRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientLoanService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplementationTest {

    private LoanServiceImplementation underTest;
    @Mock
    private LoanRepository loanRepository;
    @Mock
    private ClientLoanService clientLoanService;
    @Mock
    private ClientService clientService;
    @Mock
    private AccountService accountService;
    @Mock
    private TransactionService transactionService;

    @BeforeEach
    void arrange(){
        underTest = new LoanServiceImplementation(clientLoanService, clientService, accountService,  transactionService, loanRepository);
    }

    @Test
    void shouldFindAllLoans() {
        // Act
        underTest.findAll();

        // Assert
        verify(loanRepository).findAll();
    }

    @Test
    void shouldFindLoanById() {
        // Act
        underTest.findById(anyLong());

        // Assert
        verify(loanRepository).findLoanById(anyLong());
    }

    @Test
    void shouldTellIfExistsLoanByName() {
        // Act
        underTest.existsLoanByName(anyString());

        // Assert
        underTest.existsLoanByName(anyString());
    }

    @Test
    void shouldSaveALoan() {
        // Act
        Loan loan = mock(Loan.class);
        underTest.save(loan);

        ArgumentCaptor<Loan> argumentCaptor = ArgumentCaptor.forClass(Loan.class);

        // Assert
        verify(loanRepository).save(argumentCaptor.capture());
        Loan capturedLoan = argumentCaptor.getValue();

        assertThat(capturedLoan, is(loan));
    }

    @Test
    void shouldCreateClientLoan() {
        // Arrange
        Account acc1 = new Account("VIN001", LocalDateTime.now(), 50000, AccountType.SAVINGS);
        Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", "333");
        Loan ln1 = new Loan("Test", 500000.0, Arrays.asList(6,12,24,36,48,60), 8.0);
        LoanApplicationDTO loanApplicationDTO = new LoanApplicationDTO(0L, 20000.0, 12, "VIN001");
        Authentication authentication = mock(Authentication.class);

        // Act
        cli1.addAccount(acc1);
        given(clientService.findByEmail(authentication.getName())).willReturn(cli1);
        given(underTest.findById(anyLong())).willReturn(ln1);
        given(accountService.findAccountByNumber(loanApplicationDTO.getAssociatedAccountNumber())).willReturn(acc1);
        underTest.createClientLoan(loanApplicationDTO, authentication);

        // Assert
        verify(accountService).save(acc1);
        verify(clientLoanService).save(ArgumentMatchers.any());
        verify(transactionService).save(ArgumentMatchers.any());

        // Annihilate
        acc1 = null;
        cli1 = null;
        ln1 = null;
        loanApplicationDTO = null;
    }

    @Test
    void shouldThrowWhenThereIsANullField(){
        // Arrange
        Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", "333");
        LoanApplicationDTO loanApplicationDTO = new LoanApplicationDTO(null, null, null, "");
        Authentication authentication = mock(Authentication.class);

        // Act
        given(clientService.findByEmail(authentication.getName())).willReturn(cli1);

        // Assert
        assertThatThrownBy(()-> underTest.createClientLoan(loanApplicationDTO, authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("⋆ The following fields are empty: loan id, amount, payments, account number.");

        verify(loanRepository, never()).save(any());
        verify(transactionService, never()).save(any());
        verify(clientLoanService, never()).save(any());
    }

    @Test
    void shouldThrowIfAssociatedAccountNumberIsWrong(){
        // Arrange
        Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", "333");
        Loan ln1 = new Loan("Test", 500000.0, Arrays.asList(6,12,24,36,48,60), 8.0);
        LoanApplicationDTO loanApplicationDTO = new LoanApplicationDTO(0L, 20000.0, 12, "VIN001");
        Authentication authentication = mock(Authentication.class);

        // Act
        given(clientService.findByEmail(authentication.getName())).willReturn(cli1);
        given(underTest.findById(anyLong())).willReturn(ln1);

        // Assert
        assertThatThrownBy(()-> underTest.createClientLoan(loanApplicationDTO, authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("The given account does not exist.");

        verify(loanRepository, never()).save(any());
        verify(transactionService, never()).save(any());
        verify(accountService, never()).save(any());
    }

    @Test
    void shouldThrowWhenAccountIsNotRelatedToClient(){
        // Arrange
        Account acc1 = new Account("VIN001", LocalDateTime.now(), 50000, AccountType.SAVINGS);
        Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", "333");
        Loan ln1 = new Loan("Test", 500000.0, Arrays.asList(6,12,24,36,48,60), 8.0);
        LoanApplicationDTO loanApplicationDTO = new LoanApplicationDTO(0L, 20000.0, 12, "VIN001");
        Authentication authentication = mock(Authentication.class);

        // Act
        given(clientService.findByEmail(authentication.getName())).willReturn(cli1);
        given(underTest.findById(anyLong())).willReturn(ln1);
        given(accountService.findAccountByNumber(loanApplicationDTO.getAssociatedAccountNumber())).willReturn(acc1);

        // Assert
        assertThatThrownBy(()-> underTest.createClientLoan(loanApplicationDTO, authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("The given account is not related to the correct Client");

        verify(loanRepository, never()).save(any());
        verify(transactionService, never()).save(any());
        verify(accountService, never()).save(any());
    }

    @Test
    void shouldThrowWhenPaymentsNumberIsWrong(){
        // Arrange
        Account acc1 = new Account("VIN001", LocalDateTime.now(), 50000, AccountType.SAVINGS);
        Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", "333");
        Loan ln1 = new Loan("Test", 500000.0, Arrays.asList(6,12,24,36,48,60), 8.0);
        LoanApplicationDTO loanApplicationDTO = new LoanApplicationDTO(0L, 20000.0, 7, "VIN001");
        Authentication authentication = mock(Authentication.class);

        // Act
        cli1.addAccount(acc1);
        given(clientService.findByEmail(authentication.getName())).willReturn(cli1);
        given(underTest.findById(anyLong())).willReturn(ln1);
        given(accountService.findAccountByNumber(loanApplicationDTO.getAssociatedAccountNumber())).willReturn(acc1);

        // Assert
        assertThatThrownBy(()-> underTest.createClientLoan(loanApplicationDTO, authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("The selected payment is not among the allowed payment options.");

        verify(loanRepository, never()).save(any());
        verify(transactionService, never()).save(any());
        verify(accountService, never()).save(any());
    }

    @Test
    void shouldThrowWhenExceedLoanLimit(){
        // Arrange
        Account acc1 = new Account("VIN001", LocalDateTime.now(), 50000, AccountType.SAVINGS);
        Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", "333");
        Loan ln1 = new Loan("Test", 500_000.0, Arrays.asList(6,12,24,36,48,60), 8.0);
        LoanApplicationDTO loanApplicationDTO = new LoanApplicationDTO(0L, 200_000.0, 6, "VIN001");
        Authentication authentication = mock(Authentication.class);

        // Act
        cli1.addAccount(acc1);
        cli1.addClientLoan(new ClientLoan(cli1, ln1, 400_000, 6));
        given(clientService.findByEmail(authentication.getName())).willReturn(cli1);
        given(underTest.findById(anyLong())).willReturn(ln1);
        given(accountService.findAccountByNumber(loanApplicationDTO.getAssociatedAccountNumber())).willReturn(acc1);

        // Assert
        assertThatThrownBy(()-> underTest.createClientLoan(loanApplicationDTO, authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Max amount for this type of loan cannot be exceeded. You already have previous loans of the same type. Please, be sure not to exceed the max amount.");

        verify(loanRepository, never()).save(any());
        verify(transactionService, never()).save(any());
        verify(accountService, never()).save(any());
    }

    @Test
    void shouldThrowWhenGenericLoanIdIsWrong(){
        // Arrange
        Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", "333");
        LoanApplicationDTO loanApplicationDTO = new LoanApplicationDTO(0L, 200_000.0, 6, "VIN001");
        Authentication authentication = mock(Authentication.class);

        // Act
        given(clientService.findByEmail(authentication.getName())).willReturn(cli1);

        // Assert
        assertThatThrownBy(()-> underTest.createClientLoan(loanApplicationDTO, authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("There is not any loan with the given id.");

        verify(loanRepository, never()).save(any());
        verify(transactionService, never()).save(any());
        verify(accountService, never()).save(any());
    }

    @Test
    void shouldThrowWhenLoanApplicationAmountIsLowerThan10_000(){
        // Arrange
        Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", "333");
        Loan ln1 = new Loan("Test", 500000.0, Arrays.asList(6,12,24,36,48,60), 8.0);
        LoanApplicationDTO loanApplicationDTO = new LoanApplicationDTO(0L, 5_000.0, 6, "VIN001");
        Authentication authentication = mock(Authentication.class);

        // Act
        given(clientService.findByEmail(authentication.getName())).willReturn(cli1);
        given(underTest.findById(anyLong())).willReturn(ln1);

        // Assert
        assertThatThrownBy(()-> underTest.createClientLoan(loanApplicationDTO, authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("The loan amount must not be lower than U$S 10.0000 .");

        verify(loanRepository, never()).save(any());
        verify(transactionService, never()).save(any());
        verify(accountService, never()).save(any());
    }

    @Test
    void shouldThrowWhenLoanApplicationAmountIsOverLimit(){
        // Arrange
        Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", "333");
        Loan ln1 = new Loan("Test", 500000.0, Arrays.asList(6,12,24,36,48,60), 8.0);
        LoanApplicationDTO loanApplicationDTO = new LoanApplicationDTO(0L, 1_000_000.0, 6, "VIN001");
        Authentication authentication = mock(Authentication.class);

        // Act
        given(clientService.findByEmail(authentication.getName())).willReturn(cli1);
        given(underTest.findById(anyLong())).willReturn(ln1);

        // Assert
        assertThatThrownBy(()-> underTest.createClientLoan(loanApplicationDTO, authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("The requested amount cannot exceed the current max amount for the selected loan.");

        verify(loanRepository, never()).save(any());
        verify(transactionService, never()).save(any());
        verify(accountService, never()).save(any());
    }

    @Test
    void shouldSetGenericLoan() {
        // Arrange
        Client admin = new Client("admin", "admin", "admin@mindhub.com", "333");
        Loan ln1 = new Loan("Test", 500000.0, Arrays.asList(6,12,24,36,48,60), 8.0);
        LoanCreationDTO loanCreationDTO = new LoanCreationDTO(ln1);
        Authentication authentication = mock(Authentication.class);

        // Act
        given(clientService.findByEmail(authentication.getName())).willReturn(admin);
        underTest.setGenericLoan(loanCreationDTO, authentication);

        // Assert
        verify(loanRepository).save(ArgumentMatchers.any(Loan.class));
    }

    @Test
    void shouldThrowWhenUserIsNotAdmin(){
        // Arrange
        Client admin = new Client("test", "test", "NOTADMIN@mindhub.com", "333");
        Authentication authentication = mock(Authentication.class);
        Loan ln1 = new Loan("Test", 500000.0, Arrays.asList(6,12,24,36,48,60), 8.0);
        LoanCreationDTO loanCreationDTO = new LoanCreationDTO(ln1);

        // Act
        given(clientService.findByEmail(authentication.getName())).willReturn(admin);

        // Assert
        assertThatThrownBy(()-> underTest.setGenericLoan(loanCreationDTO, authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Only ADMIN can set a new loan.");
        verify(loanRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenThereIsANullField1(){
        // Arrange
        Client admin = new Client("admin", "admin", "admin@mindhub.com", "333");
        Authentication authentication = mock(Authentication.class);
        Loan ln1 = new Loan("", null, null, null);
        LoanCreationDTO loanCreationDTO = new LoanCreationDTO(ln1);

        // Act
        given(clientService.findByEmail(authentication.getName())).willReturn(admin);

        // Assert
        assertThatThrownBy(()-> underTest.setGenericLoan(loanCreationDTO, authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("⋆ The following fields are empty: name, max amount, payments, interest rate.");
        verify(loanRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenGenericLoanAmountIsUnder10_000(){
        // Arrange
        Client admin = new Client("admin", "admin", "admin@mindhub.com", "333");
        Authentication authentication = mock(Authentication.class);
        Loan ln1 = new Loan("Test", 1000.0, Arrays.asList(6,12,24,36,48,60), 8.0);
        LoanCreationDTO loanCreationDTO = new LoanCreationDTO(ln1);

        // Act
        given(clientService.findByEmail(authentication.getName())).willReturn(admin);

        // Assert
        assertThatThrownBy(()-> underTest.setGenericLoan(loanCreationDTO, authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Loan must be at least of 10000 dollars");
        verify(loanRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenInterestRateIsNotInRange(){
        // Arrange
        Client admin = new Client("admin", "admin", "admin@mindhub.com", "333");
        Authentication authentication = mock(Authentication.class);
        Loan ln1 = new Loan("Test", 10000.0, Arrays.asList(6,12,24,36,48,60), 108.0);
        LoanCreationDTO loanCreationDTO = new LoanCreationDTO(ln1);

        // Act
        given(clientService.findByEmail(authentication.getName())).willReturn(admin);

        // Assert
        assertThatThrownBy(()-> underTest.setGenericLoan(loanCreationDTO, authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("The interest rate must be between 0% and 100%");
        verify(loanRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenPaymentsAmountIsNotInRange(){
        // Arrange
        Client admin = new Client("admin", "admin", "admin@mindhub.com", "333");
        Authentication authentication = mock(Authentication.class);
        Loan ln1 = new Loan("Test", 10000.0, Arrays.asList(6,12,24,36,48,60,70), 8.0);
        LoanCreationDTO loanCreationDTO = new LoanCreationDTO(ln1);

        // Act
        given(clientService.findByEmail(authentication.getName())).willReturn(admin);

        // Assert
        assertThatThrownBy(()-> underTest.setGenericLoan(loanCreationDTO, authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Payment options must be higher than 0 and lower than 60.");
        verify(loanRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenGenericLoanNameAlreadyBeenTaken(){
        // Arrange
        Client admin = new Client("admin", "admin", "admin@mindhub.com", "333");
        Authentication authentication = mock(Authentication.class);
        Loan ln1 = new Loan("Test", 10000.0, Arrays.asList(6,12,24,36,48,60), 8.0);
        LoanCreationDTO loanCreationDTO = new LoanCreationDTO(ln1);

        // Act
        given(clientService.findByEmail(authentication.getName())).willReturn(admin);
        given(underTest.existsLoanByName(loanCreationDTO.getName())).willReturn(true);

        // Assert
        assertThatThrownBy(()-> underTest.setGenericLoan(loanCreationDTO, authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("There cannot be two loans with the same name");
        verify(loanRepository, never()).save(any());
    }
}