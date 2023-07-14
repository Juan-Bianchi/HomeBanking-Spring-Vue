package com.mindhub.homebanking.services.implementations;

import com.mindhub.homebanking.models.ClientLoan;
import com.mindhub.homebanking.repositories.ClientLoanRepository;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class) //turns the mocked repository into an autocloseable mock.
class ClientLoanServiceImplementationTest {

    @Mock
    private ClientLoanRepository clientLoanRepository;
    private ClientLoanServiceImplementation underTest;

    @BeforeEach
    public void arrange(){
        underTest = new ClientLoanServiceImplementation(clientLoanRepository);
    }

    @Test
    void save() {
        // Act
        ClientLoan clientLoan = mock(ClientLoan.class);
        underTest.save(clientLoan);

        ArgumentCaptor<ClientLoan> argumentCaptor = ArgumentCaptor.forClass(ClientLoan.class);

        // Assert
        verify(clientLoanRepository).save(argumentCaptor.capture());
        ClientLoan capturedClientLoan = argumentCaptor.getValue();
        assertThat(capturedClientLoan, is(clientLoan));
    }
}