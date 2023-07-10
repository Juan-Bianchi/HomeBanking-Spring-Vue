package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.dtos.CardTransactionDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import com.mindhub.homebanking.utils.CardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static com.mindhub.homebanking.utils.CardUtils.getCardNumber;

@RestController
@RequestMapping("/api")
public class CardController {

    private final ClientService clientService;
    private final CardService cardService;
    private final AccountService accountService;
    private final TransactionService transactionService;


    public CardController(ClientService clientService, CardService cardService, AccountService accountService, TransactionService transactionService){
        this.cardService = cardService;
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.clientService = clientService;
    }

    @GetMapping("/clients/current/cards")
    public Set<CardDTO> getCurrentCards(Authentication authentication){
        return clientService.findByEmail(authentication.getName()).getCards().stream().map(CardDTO::new).collect(toSet());
    }

    @GetMapping("/clients/current/activeCards")
    public Set<CardDTO> getCurrentActiveCards(Authentication authentication){
        return clientService.findByEmail(authentication.getName()).getCards().stream().filter(Card::getIsActive).map(CardDTO::new).collect(toSet());
    }

    @PostMapping("/clients/current/cards")
    public ResponseEntity<?> createCard(CardType cardType, CardColor cardColor, Authentication authentication){
        try{
            return new ResponseEntity<>(cardService.createCard(cardType, cardColor, authentication), HttpStatus.CREATED);
        }
        catch(RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PatchMapping("/clients/current/cards")
    public ResponseEntity<Object> cancelCard(@RequestParam String cardNumber, Authentication authentication){
        try{
            return new ResponseEntity<>("The card has been cancelled.", HttpStatus.ACCEPTED);
        }
        catch(RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }


    @PostMapping("/cards/transactions")
    @CrossOrigin
    @Transactional
    public ResponseEntity<Object> addCardTransaction(@RequestBody CardTransactionDTO cardTransactionDTO) {
        try{
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
        catch(RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }
}
