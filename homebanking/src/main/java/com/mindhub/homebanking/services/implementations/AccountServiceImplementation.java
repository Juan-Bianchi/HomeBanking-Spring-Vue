package com.mindhub.homebanking.services.implementations;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImplementation implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Override
    public List<Account> findAll(){

        return accountRepository.findAll();
    }

    @Override
    public Account findById(Long id){

        return  accountRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Account account) {

        accountRepository.save(account);
    }

    @Override
    public boolean existsAccountByNumber(String number) {

        return accountRepository.existsAccountByNumber(number);
    }

    @Override
    public Account findAccountByNumber(String number) {

        return accountRepository.findAccountByNumber(number);
    }
}
