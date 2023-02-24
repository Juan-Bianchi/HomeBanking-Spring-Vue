package utils;


import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.CardRepository;

public class Utilitary {
    static public String createAccountNumber(AccountRepository accountRepository){
        String numberAccount = "VIN" + String.format("%08d", (int)(Math.random()*99999999));
        while(accountRepository.existsAccountByNumber(numberAccount)){
            numberAccount = "VIN" + String.format("%08d", (int)(Math.random()*999999));
        }
        return numberAccount;
    }

    static public String createCardNumber(CardRepository cardRepository){
        String cardNumber = "";

        do{
            cardNumber = String.format("%04d", (int)(Math.random()*9999)) + "-" +
                         String.format("%04d", (int)(Math.random()*9999)) + "-" +
                         String.format("%04d", (int)(Math.random()*9999)) + "-" +
                         String.format("%04d", (int)(Math.random()*9999));
        }while(cardRepository.existsCardByNumber(cardNumber));

        return cardNumber;
    }

}
