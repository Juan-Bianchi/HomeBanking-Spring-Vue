package com.mindhub.homebanking.controllers;


import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static com.mindhub.homebanking.utils.Utilitary.createCardNumber;

@RestController
@RequestMapping("/api")
public class CardController {

    @Autowired
    CardRepository cardRepository;

    @Autowired
    ClientRepository clientRepository;

    @GetMapping("/clients/current/cards")
    public Set<CardDTO> getCurrentCards(Authentication authentication){

        return clientRepository.findByEmail(authentication.getName()).getCards().stream().map(CardDTO::new).collect(toSet());
    }

    @GetMapping("/clients/current/activeCards")
    public Set<CardDTO> getCurrentActiveCards(Authentication authentication){

        return clientRepository.findByEmail(authentication.getName()).getCards().stream().filter(Card::getIsActive).map(CardDTO::new).collect(toSet());
    }

    @PostMapping("/clients/current/cards")
    public ResponseEntity<?> createCard(CardType cardType, CardColor cardColor, Authentication authentication){

        Client client = clientRepository.findByEmail(authentication.getName());
        boolean isDuplicated = client.getCards().stream().filter(card -> card.getColor().equals(cardColor) && card.getType().equals(cardType)).count() != 0;

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
                }
                return new ResponseEntity<>("You have already using a " + cardDuplicated.getType() + cardDuplicated.getColor() + "card.", HttpStatus.FORBIDDEN);
            }

        }

        Card card = new Card(cardType, cardColor, client.getFirstName() + " " + client.getLastName(), createCardNumber(cardRepository), (int)(Math.random() * 899 + 100), LocalDate.now(), LocalDate.now().plusYears(5));
        client.addCard(card);
        cardRepository.save(card);
        CardDTO cardDto = new CardDTO(card);

        return new ResponseEntity<>(cardDto, HttpStatus.CREATED);

    }

    @PatchMapping("/clients/current/cards")
    public ResponseEntity<Object> cancelCard(@RequestParam String cardNumber, Authentication authentication){
        Client client = clientRepository.findByEmail(authentication.getName());
        Card card = cardRepository.getCardByNumber(cardNumber);
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
        cardRepository.save(card);

        return new ResponseEntity<>("The card has been cancelled.", HttpStatus.ACCEPTED);
    }
}
