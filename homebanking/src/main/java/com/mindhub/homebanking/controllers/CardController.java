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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping("/clients/current/cards")
    public Set<CardDTO> getCurrentCards(Authentication authentication){

        return clientRepository.findByEmail(authentication.getName()).getCards().stream().map(card-> new CardDTO(card)).collect(toSet());
    }

    @PostMapping("/clients/current/cards")
    public ResponseEntity<?> createCard(CardType cardType, CardColor cardColor, Authentication authentication){

        Client client = clientRepository.findByEmail(authentication.getName());
        boolean isDuplicated = client.getCards().stream().filter(card -> card.getColor().equals(cardColor) && card.getType().equals(cardType)).count() != 0;

        if(isDuplicated){
            Card cardDuplicated = client.getCards().stream().filter(card -> card.getColor().equals(cardColor) && card.getType().equals(cardType)).findFirst().orElse(null);
            if(LocalDate.now().isBefore(cardDuplicated.getThruDate().minusMonths(1))){
                if(client.getCards().stream().count() >= 6){
                    return new ResponseEntity<>("You have reached the max amount of cards available", HttpStatus.FORBIDDEN);
                }
                if(client.getCards().stream().filter(card -> card.getType().equals(cardType)).count() >= 3){
                    return new ResponseEntity<>("You have reached the max amount of " + cardType + "cards available", HttpStatus.FORBIDDEN);
                }
            }
            return new ResponseEntity<>("You have already using a " + cardDuplicated.getType() + cardDuplicated.getColor() + "card.", HttpStatus.FORBIDDEN);
        }

        Card card = new Card(cardType, cardColor, client.getFirstName() + " " + client.getLastName(), createCardNumber(cardRepository), (int)(Math.random() * 899 + 100), LocalDate.now(), LocalDate.now().plusYears(5));
        client.addCard(card);
        cardRepository.save(card);
        CardDTO cardDto = new CardDTO(card);

        return new ResponseEntity<>(cardDto, HttpStatus.CREATED);

    }
}
