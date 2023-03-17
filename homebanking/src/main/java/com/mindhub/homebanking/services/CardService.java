package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Card;

public interface CardService {

    void save(Card card);
    Card getCardByNumber(String cardNumber);
    boolean existsCardByNumber(String cardNumber);
}
