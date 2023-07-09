package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.CardTransactionDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;


public interface CardService {

    void save(Card card);
    Card getCardByNumber(String cardNumber);
    boolean existsCardByNumber(String cardNumber);
    Object createCard(CardType cardType, CardColor cardColor, Authentication authentication);
    void cancelCard(String cardNumber, Authentication authentication);
    void addCardTransaction(@RequestBody CardTransactionDTO cardTransactionDTO);
}
