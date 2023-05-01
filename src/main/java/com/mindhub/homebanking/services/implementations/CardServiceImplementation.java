package com.mindhub.homebanking.services.implementations;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardServiceImplementation implements CardService {

    @Autowired
    CardRepository cardRepository;


    @Override
    public void save(Card card) {

        cardRepository.save(card);
    }

    @Override
    public Card getCardByNumber(String cardNumber) {

        return cardRepository.getCardByNumber(cardNumber);
    }

    @Override
    public boolean existsCardByNumber(String cardNumber) {

        return cardRepository.existsCardByNumber(cardNumber);
    }
}
