package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.models.Client;
import org.springframework.http.ResponseEntity;
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
    Set<CardDTO> getCurrentCardsDTO(Client client);
    Set<CardDTO> getCurrentActiveCardsDTO(Client client);
    ResponseEntity<Object> register(@RequestParam String firstName, @RequestParam String lastName,
                                    @RequestParam String email, @RequestParam String password);
    public ResponseEntity<?> updateLastLogin(@RequestParam String email, @RequestParam String newloginDate, @RequestParam String lastLoginDate);
}

