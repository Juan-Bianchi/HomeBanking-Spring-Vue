package com.mindhub.homebanking.services.implementations;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Service
public class ClientServiceImplementation implements ClientService {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AccountRepository accountRepository;

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


}
