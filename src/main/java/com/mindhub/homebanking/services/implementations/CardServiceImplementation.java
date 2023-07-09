package com.mindhub.homebanking.services.implementations;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.dtos.CardTransactionDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import com.mindhub.homebanking.utils.CardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.mindhub.homebanking.utils.CardUtils.getCardNumber;

@Service
public class CardServiceImplementation implements CardService {

    private CardRepository cardRepository;
    private ClientService clientService;
    private AccountService accountService;
    private TransactionService transactionService;



    public CardServiceImplementation (CardRepository cardRepository, ClientService clientService, AccountService accountService, TransactionService transactionService){
        this.cardRepository = cardRepository;
        this.clientService = clientService;
        this.accountService = accountService;
        this.transactionService = transactionService;
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
    public ResponseEntity<Object> createCard(CardType cardType, CardColor cardColor, Authentication authentication) {
        Client client = clientService.findByEmail(authentication.getName());
        boolean isDuplicated = client.getCards().stream().anyMatch(card -> card.getColor().equals(cardColor) && card.getType().equals(cardType) && card.getIsActive());
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
                    return new ResponseEntity<>("You have already using a " + cardDuplicated.getType() + " " + cardDuplicated.getColor() + " card.", HttpStatus.FORBIDDEN);
                }
            }
        }

        Card card = new Card(cardType, cardColor, client.getFirstName() + " " + client.getLastName(), getCardNumber(this), CardUtils.getCVV(), LocalDate.now(), LocalDate.now().plusYears(5));
        client.addCard(card);
        clientService.save(client);
        this.save(card);
        CardDTO cardDto = new CardDTO(card);

        return new ResponseEntity<>(cardDto, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Object> cancelCard(String cardNumber, Authentication authentication) {
        Client client = clientService.findByEmail(authentication.getName());
        Card card = this.getCardByNumber(cardNumber);
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
        this.save(card);

        return new ResponseEntity<>("The card has been cancelled.", HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<Object> addCardTransaction(CardTransactionDTO cardTransactionDTO) {
        if(cardTransactionDTO.getAmount() == null || cardTransactionDTO.getCvv() == null || cardTransactionDTO.getDescription().isEmpty() || cardTransactionDTO.getNumber() == null ){
            StringBuilder strgBuilder = new StringBuilder();
            strgBuilder.append("⋆ The following fields are empty: ");
            boolean moreThanOne = false;

            if (cardTransactionDTO.getAmount() == null){
                strgBuilder.append("amount");
                moreThanOne = true;
            }
            if (cardTransactionDTO.getCvv() == null){
                if(moreThanOne){
                    strgBuilder.append(", ");
                }
                else {
                    moreThanOne = true;
                }
                strgBuilder.append("cvv");
            }
            if (cardTransactionDTO.getDescription().isEmpty()){
                if(moreThanOne){
                    strgBuilder.append(", ");
                }
                else {
                    moreThanOne = true;
                }
                strgBuilder.append("description");
            }
            if (cardTransactionDTO.getNumber().isEmpty()){
                if(moreThanOne) {
                    strgBuilder.append(", ");
                }
                strgBuilder.append("card number");
            }
            strgBuilder.append(".");

            return new ResponseEntity<>(strgBuilder.toString(), HttpStatus.FORBIDDEN);
        }
        Card card = this.getCardByNumber(cardTransactionDTO.getNumber());
        if(card == null){
            return  new ResponseEntity<>("Not existing card.", HttpStatus.FORBIDDEN);
        }
        if(card.getCvv() != cardTransactionDTO.getCvv()){
            return new ResponseEntity<>("The provided cvv is not correct.", HttpStatus.FORBIDDEN);
        }
        if(card.getType() == CardType.CREDIT){
            return new ResponseEntity<>("The card is not a Debit Card.", HttpStatus.FORBIDDEN);
        }
        Account account = card.getClient().getAccounts().stream().filter(acc -> acc.getType() == AccountType.SAVINGS && acc.getBalance() >= cardTransactionDTO.getAmount()).findFirst().orElse(null);
        if(account == null){
            return new ResponseEntity<>("You do not have enough money in your accounts to complete the transaction.", HttpStatus.FORBIDDEN);
        }

        Transaction transaction = new Transaction(TransactionType.DEBIT, cardTransactionDTO.getAmount(), cardTransactionDTO.getDescription(), LocalDateTime.now());
        account.addTransaction(transaction);
        account.setBalance(account.getBalance() - cardTransactionDTO.getAmount());

        accountService.save(account);
        transactionService.save(transaction);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
