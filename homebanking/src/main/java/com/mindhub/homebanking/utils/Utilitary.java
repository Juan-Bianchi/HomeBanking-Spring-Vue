package com.mindhub.homebanking.utils;


import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;

import java.time.LocalDateTime;

public class Utilitary {

    static public String createAccountNumber(AccountService accountService){
        String numberAccount = "VIN" + String.format("%08d", (int)(Math.random()*99999999));
        while(accountService.existsAccountByNumber(numberAccount)){
            numberAccount = "VIN" + String.format("%08d", (int)(Math.random()*999999));
        }

        return numberAccount;
    }

    static public String createCardNumber(CardService cardService){
        String cardNumber = "";

        do{
            cardNumber = String.format("%04d", (int)(Math.random()*9999)) + "-" +
                         String.format("%04d", (int)(Math.random()*9999)) + "-" +
                         String.format("%04d", (int)(Math.random()*9999)) + "-" +
                         String.format("%04d", (int)(Math.random()*9999));
        }while(cardService.existsCardByNumber(cardNumber));

        return cardNumber;
    }
}
