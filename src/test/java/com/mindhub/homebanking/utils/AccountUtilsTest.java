package com.mindhub.homebanking.utils;

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.implementations.AccountServiceImplementation;
import net.bytebuddy.pool.TypePool;
import org.aspectj.weaver.ast.Not;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class AccountUtilsTest {

    private AccountServiceImplementation accountServiceImplementation;
    private AccountRepository accountRepository;
    private ClientService clientService;

    @BeforeEach
    public void arrange(){
        accountServiceImplementation = new AccountServiceImplementation(accountRepository, clientService);
    }



}