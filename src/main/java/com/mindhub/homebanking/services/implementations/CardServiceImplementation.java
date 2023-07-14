package com.mindhub.homebanking.services.implementations;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.dtos.CardTransactionDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Service
public class CardServiceImplementation implements CardService {

    private final CardRepository cardRepository;
    private final ClientService clientService;
    private final TransactionService transactionService;
    private final AccountService accountService;

    public CardServiceImplementation (CardRepository cardRepository, ClientService clientService, TransactionService transactionService, AccountService accountService){
        this.cardRepository = cardRepository;
        this.clientService = clientService;
        this.transactionService = transactionService;
        this.accountService = accountService;
    }


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

    @Override
    public CardDTO createCard(CardType cardType, CardColor cardColor, Authentication authentication) {
        Client client = clientService.findByEmail(authentication.getName());
        boolean isDuplicated = client.getCards().stream().anyMatch(card -> card.getColor().equals(cardColor) && card.getType().equals(cardType) && card.getIsActive());
        if(isDuplicated){
            Card cardDuplicated = client.getCards().stream().filter(card -> card.getColor().equals(cardColor) && card.getType().equals(cardType)).findFirst().orElse(null);
            if(cardDuplicated != null){
                if(LocalDate.now().isBefore(cardDuplicated.getThruDate().minusMonths(1))){
                    if((long) client.getCards().size() >= 6){
                       throw new RuntimeException("You have reached the max amount of cards available");
                    }
                    if(client.getCards().stream().filter(card -> card.getType().equals(cardType)).count() >= 3){
                        throw new RuntimeException("You have reached the max amount of " + cardType + "cards available");
                    }
                    throw new RuntimeException("You have already using a " + cardDuplicated.getType() + " " + cardDuplicated.getColor() + " card.");
                }
            }
        }

        Card card = new Card(cardType, cardColor, client.getFirstName() + " " + client.getLastName(), getCardNumber(), getCVV(), LocalDate.now(), LocalDate.now().plusYears(5));
        client.addCard(card);
        clientService.save(client);
        this.save(card);

        return new CardDTO(card);
    }

    @Override
    public void cancelCard(String cardNumber, Authentication authentication) {
        Client client = clientService.findByEmail(authentication.getName());
        Card card = this.getCardByNumber(cardNumber);
        if(cardNumber.isEmpty()){
            throw new RuntimeException("Card number cannot be empty");
        }
        if(card == null){
            throw new RuntimeException("Card does not exist.");
        }
        if(!client.getCards().contains(card)){
            throw new RuntimeException("This card does not belong to the current user.");
        }

        card.setIsActive(false);
        this.save(card);
    }

    @Override
    public void addCardTransaction(CardTransactionDTO cardTransactionDTO) {

        if(thereIsNullField(cardTransactionDTO)){
            String errorMessage = verifyNullFields(cardTransactionDTO);
            throw new RuntimeException(errorMessage);
        }
        Card card = this.getCardByNumber(cardTransactionDTO.getNumber());
        if(card == null){
            throw new RuntimeException("Not existing card.");
        }
        if(card.getCvv() != cardTransactionDTO.getCvv()){
            throw new RuntimeException("The provided cvv is not correct.");
        }
        if(card.getType() == CardType.CREDIT){
            throw new RuntimeException("The card is not a Debit Card.");
        }
        Account account = card.getClient().getAccounts().stream().filter(acc -> acc.getType() == AccountType.SAVINGS && acc.getBalance() >= cardTransactionDTO.getAmount()).findFirst().orElse(null);
        if(account == null){
            throw new RuntimeException("You do not have enough money in your accounts to complete the transaction.");
        }

        Transaction transaction = new Transaction(TransactionType.DEBIT, cardTransactionDTO.getAmount(), cardTransactionDTO.getDescription(), LocalDateTime.now());
        account.addTransaction(transaction);
        account.setBalance(account.getBalance() - cardTransactionDTO.getAmount());

        accountService.save(account);
        transactionService.save(transaction);
    }

    @Override
    public String getCardNumber(){
        String cardNumber;

        do{
            cardNumber = String.format("%04d", (int)(Math.random()*9999)) + "-" +
                    String.format("%04d", (int)(Math.random()*9999)) + "-" +
                    String.format("%04d", (int)(Math.random()*9999)) + "-" +
                    String.format("%04d", (int)(Math.random()*9999));
        }while(this.existsCardByNumber(cardNumber));

        return cardNumber;
    }

    @Override
    public int getCVV() {
        return (int) (Math.random() * 899 + 100);
    }



    //AUXILIARY METHODS
    private boolean thereIsNullField(CardTransactionDTO cardTransactionDTO){
        return cardTransactionDTO.getAmount() == null || cardTransactionDTO.getCvv() == null || cardTransactionDTO.getDescription().isEmpty() || cardTransactionDTO.getNumber() == null;
    }

    private String verifyNullFields(CardTransactionDTO cardTransactionDTO){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("⋆ The following fields are empty: ");
        boolean moreThanOne = false;

        if (cardTransactionDTO.getAmount() == null){
            stringBuilder.append("amount");
            moreThanOne = true;
        }
        if (cardTransactionDTO.getCvv() == null){
            if(moreThanOne){
                stringBuilder.append(", ");
            }
            else {
                moreThanOne = true;
            }
            stringBuilder.append("cvv");
        }
        if (cardTransactionDTO.getDescription().isEmpty()){
            if(moreThanOne){
                stringBuilder.append(", ");
            }
            else {
                moreThanOne = true;
            }
            stringBuilder.append("description");
        }
        if (cardTransactionDTO.getNumber().isEmpty()){
            if(moreThanOne) {
                stringBuilder.append(", ");
            }
            stringBuilder.append("card number");
        }
        stringBuilder.append(".");

        return stringBuilder.toString();
    }


}
