package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.dtos.CardTransactionDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api")
public class CardController {

    private final ClientService clientService;
    private final CardService cardService;

    public CardController (ClientService clientService, CardService cardService, AccountService accountService, TransactionService transactionService) {
        this.clientService = clientService;
        this.cardService = cardService;
    }

    @GetMapping("/clients/current/cards")
    public Set<CardDTO> getCurrentCards(Authentication authentication){
        return clientService.getCurrentCardsDTO(clientService.findByEmail(authentication.getName()));
    }

    @GetMapping("/clients/current/activeCards")
    public Set<CardDTO> getCurrentActiveCards(Authentication authentication){
        return clientService.getCurrentCardsDTO(clientService.findByEmail(authentication.getName()));
    }

    @PostMapping("/clients/current/cards")
    public ResponseEntity<Object> createCard(CardType cardType, CardColor cardColor, Authentication authentication){
        return  cardService.createCard(cardType, cardColor, authentication);
    }

    @PatchMapping("/clients/current/cards")
    public ResponseEntity<Object> cancelCard(@RequestParam String cardNumber, Authentication authentication){
        return cardService.cancelCard(cardNumber, authentication);
    }


    @PostMapping("/cards/transactions")
    @CrossOrigin
    @Transactional
    public ResponseEntity<Object> addCardTransaction(@RequestBody CardTransactionDTO cardTransactionDTO) {
        return cardService.addCardTransaction(cardTransactionDTO);
    }
}
