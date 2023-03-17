package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;

import java.util.List;

public interface ClientService {
    List<Client> findAll();

    Client findById(Long id);

    Client findByEmail(String email);

    void save(Client client);


}

