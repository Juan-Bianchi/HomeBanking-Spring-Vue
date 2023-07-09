package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.dtos.CardTransactionDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.http.HttpStatus;
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
        try{
            return new ResponseEntity<>(cardService.createCard(cardType, cardColor, authentication), HttpStatus.CREATED);
        }
        catch (RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PatchMapping("/clients/current/cards")
    public ResponseEntity<Object> cancelCard(@RequestParam String cardNumber, Authentication authentication){
        try{
            cardService.cancelCard(cardNumber, authentication);
            return new ResponseEntity<>("The card has been cancelled.", HttpStatus.ACCEPTED);
        }
        catch (RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }


    @PostMapping("/cards/transactions")
    @CrossOrigin
    @Transactional
    public ResponseEntity<Object> addCardTransaction(@RequestBody CardTransactionDTO cardTransactionDTO) {
        try{
            cardService.addCardTransaction(cardTransactionDTO);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
        catch (RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }
}
