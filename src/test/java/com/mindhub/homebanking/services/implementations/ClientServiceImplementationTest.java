package com.mindhub.homebanking.services.implementations;

import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;


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
    void findAll() {

    }

    @Test
    void findById() {
    }

    @Test
    void findByEmail() {
    }

    @Test
    void save() {
    }

    @Test
    void getAccountsDTO() {
    }

    @Test
    void getActiveAccountsDTO() {
    }

    @Test
    void register() {
    }

    @Test
    void updateLastLogin() {
    }
}