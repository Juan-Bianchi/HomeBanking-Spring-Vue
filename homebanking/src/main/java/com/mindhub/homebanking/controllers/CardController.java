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

    @Autowired
    ClientService clientService;
    @Autowired
    CardService cardService;
    @Autowired
    AccountService accountService;
    @Autowired
    TransactionService transactionService;


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

        Client client = clientService.findByEmail(authentication.getName());
        boolean isDuplicated = client.getCards().stream().anyMatch(card -> card.getColor().equals(cardColor) && card.getType().equals(cardType) && card.getIsActive());
        if(isDuplicated){
            Card cardDuplicated = client.getCards().stream().filter(card -> card.getColor().equals(cardColor) && card.getType().equals(cardType)).findFirst().orElse(null);
            if(cardDuplicated != null){
                if(LocalDate.now().isBefore(cardDuplicated.getThruDate().minusMonths(1))){
                    if((long) client.getCards().size() >= 6){
                        return new ResponseEntity<>("You have reached the max amount of cards available", HttpStatus.FORBIDDEN);
                    }
                    if(client.getCards().stream().filter(card -> card.getType().equals(cardType)).count() >= 3){
                        return new ResponseEntity<>("You have reached the max amount of " + cardType + "cards available", HttpStatus.FORBIDDEN);
                    }
                    return new ResponseEntity<>("You have already using a " + cardDuplicated.getType() + " " + cardDuplicated.getColor() + " card.", HttpStatus.FORBIDDEN);
                }
            }
        }

        Card card = new Card(cardType, cardColor, client.getFirstName() + " " + client.getLastName(), getCardNumber(cardService), CardUtils.getCVV(), LocalDate.now(), LocalDate.now().plusYears(5));
        client.addCard(card);
        clientService.save(client);
        cardService.save(card);
        CardDTO cardDto = new CardDTO(card);

        return new ResponseEntity<>(cardDto, HttpStatus.CREATED);

    }

    @PatchMapping("/clients/current/cards")
    public ResponseEntity<Object> cancelCard(@RequestParam String cardNumber, Authentication authentication){
        Client client = clientService.findByEmail(authentication.getName());
        Card card = cardService.getCardByNumber(cardNumber);
        if(cardNumber.isEmpty()){
            return new ResponseEntity<>("Card number cannot be empty", HttpStatus.FORBIDDEN);
        }
        if(card == null){

            return  new ResponseEntity<>("Card does not exist.", HttpStatus.FORBIDDEN);
        }
        if(!client.getCards().contains(card)){

            return new ResponseEntity<>("This card does not belong to the current user.", HttpStatus.FORBIDDEN);
        }

        card.setIsActive(false);
        cardService.save(card);

        return new ResponseEntity<>("The card has been cancelled.", HttpStatus.ACCEPTED);
    }


    @PostMapping("/cards/transactions")
    @CrossOrigin
    @Transactional
    public ResponseEntity<Object> addCardTransaction(@RequestBody CardTransactionDTO cardTransactionDTO) {

        if(cardTransactionDTO.getAmount() == null || cardTransactionDTO.getCvv() == null || cardTransactionDTO.getDescription().isEmpty() || cardTransactionDTO.getNumber() == null ){
            String errorMessage = "⋆ The following fields are empty: ";
            boolean moreThanOne = false;

            if (cardTransactionDTO.getAmount() == null){
                errorMessage += "amount";
                moreThanOne = true;
            }
            if (cardTransactionDTO.getCvv() == null){
                if(moreThanOne){
                    errorMessage += ", ";
                }
                else {
                    moreThanOne = true;
                }
                errorMessage += "cvv";
            }
            if (cardTransactionDTO.getDescription().isEmpty()){
                if(moreThanOne){
                    errorMessage += ", ";
                }
                else {
                    moreThanOne = true;
                }
                errorMessage += "description";
            }
            if (cardTransactionDTO.getNumber().isEmpty()){
                if(moreThanOne) {
                    errorMessage += ", ";
                }
                errorMessage += "card number";
            }
            errorMessage += ".";

            return new ResponseEntity<>(errorMessage, HttpStatus.FORBIDDEN);
        }
        Card card = cardService.getCardByNumber(cardTransactionDTO.getNumber());
        if(card == null){

            return  new ResponseEntity<>("Not existing card.", HttpStatus.FORBIDDEN);
        }
        if(card.getCvv() != cardTransactionDTO.getCvv()){

            return new ResponseEntity<>("The provided cvv is not correct.", HttpStatus.FORBIDDEN);
        }
        if(card.getType() == CardType.CREDIT){

            return new ResponseEntity<>("The card is not a Debit Card.", HttpStatus.FORBIDDEN);
        }
        Account account = card.getClient().getAccounts().stream().filter(acc -> acc.getType() == AccountType.SAVINGS && acc.getBalance() >= cardTransactionDTO.getAmount()).findFirst().orElse(null);
        if(account == null){

            return new ResponseEntity<>("You do not have enough money in your accounts to complete the transaction.", HttpStatus.FORBIDDEN);
        }

        Transaction transaction = new Transaction(TransactionType.DEBIT, cardTransactionDTO.getAmount(), cardTransactionDTO.getDescription(), LocalDateTime.now());
        account.addTransaction(transaction);
        account.setBalance(account.getBalance() - cardTransactionDTO.getAmount());

        accountService.save(account);
        transactionService.save(transaction);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
