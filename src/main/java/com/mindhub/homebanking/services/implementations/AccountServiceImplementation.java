package com.mindhub.homebanking.services.implementations;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Service
public class AccountServiceImplementation implements AccountService {

    private final AccountRepository accountRepository;
    private final ClientService clientService;

    public AccountServiceImplementation(AccountRepository accountRepository, @Lazy ClientService clientService){
        this.clientService = clientService;
        this.accountRepository = accountRepository;
    }

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
    public AccountDTO createAccount(Authentication authentication, AccountType accountType) {
        Client client =  clientService.findByEmail(authentication.getName());
        if(client.getAccounts().stream().filter(Account::getIsActive).count() >= 3){
            throw new RuntimeException("The max amount of accounts have been already created");
        }

        String accountNumber = createAccountNumber();
        Account account = new Account(accountNumber, LocalDateTime.now(), 0, accountType);
        client.addAccount(account);
        this.save(account);
        clientService.save(client);

        return new AccountDTO(account);
    }

    @Override
    public void cancelAccount(String number, Authentication authentication) {
        if(number.isEmpty()){
            throw new RuntimeException("Account number is empty. It must be provided.");
        }
        Client client =  clientService.findByEmail(authentication.getName());
        Account account = this.findAccountByNumber(number);
        if(account == null){
            throw new RuntimeException("The account does not exist.");
        }
        if(!client.getAccounts().contains(account)){
            throw new RuntimeException("This account does not belong to the current user.");
        }
        if(account.getBalance() > 0){
            throw new RuntimeException("You cannot delete an account if there is money in it.");
        }

        account.setIsActive(false);
        this.save(account);
    }

    @Override
    public String createAccountNumber(){
        String numberAccount;
        do {
            numberAccount = "VIN" + String.format("%08d", (int)(Math.random()*999999));
        } while(existsAccountByNumber(numberAccount));

        return numberAccount;
    }
}
