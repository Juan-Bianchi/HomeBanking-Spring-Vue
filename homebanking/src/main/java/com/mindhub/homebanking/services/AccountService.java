package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;

import java.util.List;

public interface AccountService {

    List<Account> findAll();
    Account findById(Long id);
    void save(Account account);
    boolean existsAccountByNumber(String number);
    Account findAccountByNumber(String number);
}
