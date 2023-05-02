package com.mindhub.homebanking.services.implementations;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.mindhub.homebanking.utils.AccountUtils.createAccountNumber;
import static java.util.stream.Collectors.toSet;

@Service
public class AccountServiceImplementation implements AccountService {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ClientService clientService;

    @Override
    public Set<AccountDTO> findAll(){

        return accountRepository.findAll().stream().map(account -> new AccountDTO(account)).collect(toSet());
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

    @Override
    public ResponseEntity<Object> createAccount(Authentication authentication, AccountType accountType) {

        Client client =  clientService.findByEmail(authentication.getName());
        if(client.getAccounts().stream().filter(Account::getIsActive).count() >= 3){

            return new ResponseEntity<>("The max amount of accounts have been already created", HttpStatus.FORBIDDEN);
        }

        String accountNumber = createAccountNumber(this);
        Account account = new Account(accountNumber, LocalDateTime.now(), 0, accountType);
        client.addAccount(account);
        this.save(account);
        clientService.save(client);
        AccountDTO accountDTO = new AccountDTO(account);

        return new ResponseEntity<>(accountDTO, HttpStatus.CREATED);

    }

    @Override
    public ResponseEntity<Object> cancelAccount(String number, Authentication authentication) {

        if(number.isEmpty()){

            return new ResponseEntity<>("Account number is empty. It must be provided.", HttpStatus.FORBIDDEN);
        }
        Client client =  clientService.findByEmail(authentication.getName());
        Account account = this.findAccountByNumber(number);
        if(account == null){

            return new ResponseEntity<>("The account does not exist.", HttpStatus.FORBIDDEN);
        }
        if(!client.getAccounts().contains(account)){

            return new ResponseEntity<>("This account does not belong to the current user.", HttpStatus.FORBIDDEN);
        }
        if(account.getBalance() > 0){

            return new ResponseEntity<>("You cannot delete an account if there is money in it.", HttpStatus.FORBIDDEN);
        }

        account.setIsActive(false);
        this.save(account);

        return new ResponseEntity<>("The account was cancelled", HttpStatus.ACCEPTED);
    }
}
