package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.dtos.CardTransactionDTO;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    private CardController underTest;
    @Mock
    private CardService cardService;
    @Mock
    private ClientService clientService;
    @Mock
    private TransactionService transactionService;


    @BeforeEach
    void arrange(){
        underTest = new CardController(clientService, cardService, transactionService);
    }

    @Test
    void shouldGetCurrentCards() {
        // Arrange
        Authentication authentication = mock(Authentication.class);

        // Act
        Set<CardDTO> obtainedCards = underTest.getCurrentCards(authentication);

        // Assert
        assertThat(obtainedCards, notNullValue());
    }

    @Test
    void shouldGetCurrentActiveCards() {
        // Arrange
        Authentication authentication = mock(Authentication.class);

        // Act
        Set<CardDTO> obtainedCards = underTest.getCurrentActiveCards(authentication);

        // Assert
        assertThat(obtainedCards, notNullValue());
    }

    @Test
    void shouldCreateCard() {
        // Arrange
        CardDTO expectedCardDTO = new CardDTO();
        Authentication authentication = mock(Authentication.class);
        CardType cardType = CardType.CREDIT;
        CardColor cardColor = CardColor.GOLD;
        ResponseEntity<?> expectedResponseEntity = new ResponseEntity<>(expectedCardDTO, HttpStatus.CREATED);

        // Act
        given(cardService.createCard(cardType, cardColor, authentication)).willReturn(expectedCardDTO);
        ResponseEntity<?> obtainedResponseEntity = underTest.createCard(cardType, cardColor, authentication);

        // Assert
        assertThat(expectedResponseEntity, is(obtainedResponseEntity));
    }

    @Test
    void shouldCatchWhenAnExceptionIsThrownWhileCreatingCard(){
        // Arrange
        Authentication authentication = mock(Authentication.class);
        ResponseEntity<?> expectedResponseEntity = new ResponseEntity<>("testError", HttpStatus.FORBIDDEN);

        // Act
        doThrow(new RuntimeException("testError")).when(cardService).createCard(null, null, authentication);
        ResponseEntity<?> obtainedResponseEntity = underTest.createCard(null, null, authentication);

        // Assert
        assertThat(expectedResponseEntity, is(obtainedResponseEntity));
    }

    @Test
    void shouldCancelCard() {
        // Arrange
        String cardNumber = "test";
        Authentication authentication = mock(Authentication.class);
        ResponseEntity<Object> expectedResponseEntity =  new ResponseEntity<>("The card has been cancelled.", HttpStatus.ACCEPTED);

        // Act
        ResponseEntity<Object> obtainedResponseEntity = underTest.cancelCard(cardNumber, authentication);

        // Assert
        assertThat(expectedResponseEntity, is(obtainedResponseEntity));
    }

    @Test
    void shouldCatchWhenAnExceptionIsThrownWhileCancellingCard(){
        // Arrange
        String cardNumber = "";
        Authentication authentication = mock(Authentication.class);
        ResponseEntity<Object> expectedResponseEntity =  new ResponseEntity<>("Card number cannot be empty", HttpStatus.FORBIDDEN);

        // Act
        doThrow(new RuntimeException("Card number cannot be empty")).when(cardService).cancelCard(cardNumber, authentication);
        ResponseEntity<Object> obtainedResponseEntity = underTest.cancelCard(cardNumber, authentication);

        // Assert
        assertThat(expectedResponseEntity, is(obtainedResponseEntity));
    }

    @Test
    void shouldAddCardTransaction() {
        // Arrange
        CardTransactionDTO expectedCardTransactionDTO = mock(CardTransactionDTO.class);
        ResponseEntity<Object> expectedResponseEntity = new ResponseEntity<>(HttpStatus.ACCEPTED);

        // Act
        ResponseEntity<Object> obtainedResponseEntity = underTest.addCardTransaction(expectedCardTransactionDTO);

        // Assert
        assertThat(expectedResponseEntity, is(obtainedResponseEntity));
    }

    @Test
    void shouldCatchWhenAnExceptionIsThrownWhileAddingTransaction(){
        // Arrange
        CardTransactionDTO cardTransactionDTO = new CardTransactionDTO();
        ResponseEntity<Object> expectedResponseEntity = new ResponseEntity<>("Not existing card.", HttpStatus.FORBIDDEN);

        // Act
        doThrow(new RuntimeException("Not existing card.")).when(cardService).addCardTransaction(ArgumentMatchers.any(CardTransactionDTO.class));
        ResponseEntity<Object> obtainedResponseEntity = underTest.addCardTransaction(cardTransactionDTO);

        // Assert
        assertThat(obtainedResponseEntity, is(expectedResponseEntity));
    }
}