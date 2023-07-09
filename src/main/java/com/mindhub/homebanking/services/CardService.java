package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.dtos.CardTransactionDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

public interface CardService {

    void save(Card card);
    Card getCardByNumber(String cardNumber);
    boolean existsCardByNumber(String cardNumber);
    ResponseEntity<Object> createCard(CardType cardType, CardColor cardColor, Authentication authentication);
    ResponseEntity<Object> cancelCard(String cardNumber, Authentication authentication);
    ResponseEntity<Object> addCardTransaction(@RequestBody CardTransactionDTO cardTransactionDTO);
}
