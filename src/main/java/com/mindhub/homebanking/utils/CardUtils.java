package com.mindhub.homebanking.utils;

import com.mindhub.homebanking.services.CardService;

public final class CardUtils {

    private CardUtils(){}

    static public String getCardNumber(CardService cardService){
        String cardNumber;

        do{
            cardNumber = String.format("%04d", (int)(Math.random()*9999)) + "-" +
                         String.format("%04d", (int)(Math.random()*9999)) + "-" +
                         String.format("%04d", (int)(Math.random()*9999)) + "-" +
                         String.format("%04d", (int)(Math.random()*9999));
        }while(cardService.existsCardByNumber(cardNumber));

        return cardNumber;
    }


    public static int getCVV() {
        return (int) (Math.random() * 899 + 100);
    }
}
