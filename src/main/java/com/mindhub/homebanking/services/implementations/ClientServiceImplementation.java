package com.mindhub.homebanking.services.implementations;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.mindhub.homebanking.utils.AccountUtils.createAccountNumber;
import static java.util.stream.Collectors.toSet;

@Service
public class ClientServiceImplementation implements ClientService {

    private final ClientRepository clientRepository;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    public ClientServiceImplementation (ClientRepository clientRepository, @Lazy AccountService accountService, PasswordEncoder passwordEncoder){
        this.clientRepository = clientRepository;
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public Client findById(Long id){
        return clientRepository.findById(id).orElse(null);
    }

    @Override
    public Client findByEmail(String email){
        return clientRepository.findByEmail(email);
    }

    @Override
    public void save(Client client){
        clientRepository.save(client);
    }

    @Override
    public Set<AccountDTO> getAccountsDTO(Client client) {
        return  client.getAccounts().stream().map(AccountDTO::new).collect(toSet());
    }

    @Override
    public Set<AccountDTO> getActiveAccountsDTO(Client client) {
        return client.getAccounts().stream().filter(Account::getIsActive).map(AccountDTO::new).collect(toSet());
    }

    @Override
    public Set<CardDTO> getCurrentCardsDTO(Client client) {
        return client.getCards().stream().map(CardDTO::new).collect(toSet());
    }

    @Override
    public Set<CardDTO> getCurrentActiveCardsDTO(Client client) {
        return client.getCards().stream().filter(Card::getIsActive).map(CardDTO::new).collect(toSet());
    }

    @Override
    public void register(String firstName, String lastName, String email, String password) {
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            String errorMessage = "⋆ Please, fill the following fields: ";
            boolean moreThanOne = false;

            if (firstName.isEmpty()){
                errorMessage += "first names";
                moreThanOne = true;
            }
            if (lastName.isEmpty()){
                if(moreThanOne){
                    errorMessage += ", ";
                }
                else {
                    moreThanOne = true;
                }
                errorMessage += "last names";
            }
            if (email.isEmpty()){
                if(moreThanOne){
                    errorMessage += ", ";
                }
                else {
                    moreThanOne = true;
                }
                errorMessage += "email";
            }
            if (password.isEmpty()){
                if(moreThanOne) {
                    errorMessage += ", ";
                }
                errorMessage += "password";
            }
            errorMessage += ".";
            throw new RuntimeException(errorMessage);
        }
        if (this.findByEmail(email) !=  null) {
            throw new RuntimeException("⋆ Email already in use");
        }

        Client client = new Client(firstName, lastName, email, passwordEncoder.encode(password));
        String accountNumber = createAccountNumber(accountService);
        Account account = new Account(accountNumber, LocalDateTime.now(), 0, AccountType.SAVINGS);
        client.addAccount(account);
        this.save(client);
        accountService.save(account);
    }

    @Override
    public void updateLastLogin(String email, String newloginDate, String lastLoginDate) {
        Client client = this.findByEmail(email);
        if(client == null){
            throw new RuntimeException("Client does not exist.");
        }
        client.setLastLogin(lastLoginDate);
        client.setNewLogin(newloginDate);
        this.save(client);
    }


}
