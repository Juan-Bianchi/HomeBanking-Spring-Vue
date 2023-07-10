package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

public interface AccountService {

    Set<AccountDTO> findAll();
    Account findById(Long id);
    void save(Account account);
    boolean existsAccountByNumber(String number);
    Account findAccountByNumber(String number);
    AccountDTO createAccount(Authentication authentication, AccountType accountType);
    void cancelAccount(String number, Authentication authentication);
}
