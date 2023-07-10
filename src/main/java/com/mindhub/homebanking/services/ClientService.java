package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Client;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

public interface ClientService {

    List<Client> findAll();
    Client findById(Long id);
    Client findByEmail(String email);
    void save(Client client);
    Set<AccountDTO> getAccountsDTO(Client client);
    Set<AccountDTO> getActiveAccountsDTO(Client client);
    void register(@RequestParam String firstName, @RequestParam String lastName,
                                    @RequestParam String email, @RequestParam String password);
    void updateLastLogin(@RequestParam String email, @RequestParam String newloginDate, @RequestParam String lastLoginDate);
}

