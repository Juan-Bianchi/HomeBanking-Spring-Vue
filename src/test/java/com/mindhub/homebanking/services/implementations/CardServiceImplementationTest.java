package com.mindhub.homebanking.services.implementations;

import com.mindhub.homebanking.dtos.CardTransactionDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) //turns the mocked repository into an autocloseable mock.
class CardServiceImplementationTest {

    private CardServiceImplementation underTest;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private ClientService clientService;
    @Mock
    private AccountService accountService;
    @Mock
    private TransactionService transactionService;

    @BeforeEach
    public void arrange(){
        underTest = new CardServiceImplementation(cardRepository, clientService, transactionService, accountService);
    }

    @Test
    void shouldSaveCard() {
        // Act
        Card card = mock(Card.class);
        underTest.save(card);

        ArgumentCaptor<Card> argumentCaptor = ArgumentCaptor.forClass(Card.class);

        // Assert
        verify(cardRepository).save(argumentCaptor.capture());
        Card capturedCard = argumentCaptor.getValue();
        assertThat(capturedCard, is(card));
    }

    @Test
    void shouldGetCardByNumber() {
        // Act
        underTest.getCardByNumber(anyString());

        // Assert
        verify(cardRepository).getCardByNumber(anyString());
    }

    @Test
    void shouldTellIfExistsCardByNumber() {
        // Act
        underTest.getCardByNumber(anyString());

        // Assert
        verify(cardRepository).getCardByNumber(anyString());
    }

    @Test
    void shouldCreateCard() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        Client cli = new Client();

        // Act
        given(clientService.findByEmail(authentication.getName())).willReturn(cli);
        underTest.createCard(CardType.CREDIT, CardColor.GOLD, authentication);

        // Assert
        verify(cardRepository).save(ArgumentMatchers.any(Card.class));
    }

    @Test
    void shouldThrowWhenThereAreMoreThan6Cards(){
        // Arrange
        Authentication authentication = mock(Authentication.class);
        Client cli = new Client();
        Card card = new Card(CardType.DEBIT, CardColor.GOLD, "Melba Morel", "1010-0020-3853-7754", 123, LocalDate.now(), LocalDate.now().plusYears(5));

        // Act
        cli.addCard(card);
        for(int i = 0; i < 5; i++){
            cli.addCard(new Card(CardType.DEBIT, CardColor.GOLD, "Melba Morel", "1010-0020-3853-7754", 123, LocalDate.now(), LocalDate.now().plusYears(5)));
        }

        given(clientService.findByEmail(authentication.getName())).willReturn(cli);

        // Assert
        assertThatThrownBy(() -> underTest.createCard(CardType.DEBIT, CardColor.GOLD, authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("You have reached the max amount of cards available");

        verify(cardRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCardTypeHasMoreThan3Cards(){
        // Arrange
        Authentication authentication = mock(Authentication.class);
        Client cli = new Client();
        Card card = new Card(CardType.DEBIT, CardColor.GOLD, "Melba Morel", "1010-0020-3853-7754", 123, LocalDate.now(), LocalDate.now().plusYears(5));
        Card card1 = new Card(CardType.DEBIT, CardColor.SILVER, "Melba Morel", "1010-0020-3853-7755", 123, LocalDate.now(), LocalDate.now().plusYears(5));
        Card card2 = new Card(CardType.DEBIT, CardColor.TITANIUM, "Melba Morel", "1010-0020-3853-7756", 123, LocalDate.now(), LocalDate.now().plusYears(5));

        // Act
        cli.addCard(card);
        cli.addCard(card1);
        cli.addCard(card2);

        given(clientService.findByEmail(authentication.getName())).willReturn(cli);

        // Assert
        assertThatThrownBy(() -> underTest.createCard(CardType.DEBIT, CardColor.GOLD, authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("You have reached the max amount of " + CardType.DEBIT + "cards available");

        verify(cardRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenClientHasAlreadyTheCardType(){
        // Arrange
        Authentication authentication = mock(Authentication.class);
        Client cli = new Client();
        Card card = new Card(CardType.DEBIT, CardColor.GOLD, "Melba Morel", "1010-0020-3853-7754", 123, LocalDate.now(), LocalDate.now().plusYears(5));

        // Act
        cli.addCard(card);
        given(clientService.findByEmail(authentication.getName())).willReturn(cli);

        // Assert
        assertThatThrownBy(() -> underTest.createCard(CardType.DEBIT, CardColor.GOLD, authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("You have already using a " + card.getType() + " " + card.getColor() + " card.");

        verify(cardRepository, never()).save(any());
    }

    @Test
    void shouldCancelCard() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        Client client = new Client();
        Card card = new Card(CardType.DEBIT, CardColor.GOLD, "Melba Morel", "1010-0020-3853-7754", 123, LocalDate.now(), LocalDate.now().plusYears(5));

        // Act
        client.addCard(card);
        given(clientService.findByEmail(authentication.getName())).willReturn(client);
        given(underTest.getCardByNumber(card.getNumber())).willReturn(card);
        underTest.cancelCard(card.getNumber(),authentication);

        // Assert
        verify(cardRepository).save(card);
    }

    @Test
    void shouldThrowWhenCardNumberIsEmpty(){
        // Arrange
        Authentication authentication = mock(Authentication.class);
        Client client = new Client();
        Card card = new Card(CardType.DEBIT, CardColor.GOLD, "Melba Morel", "1010-0020-3853-7754", 123, LocalDate.now(), LocalDate.now().plusYears(5));

        // Act
        client.addCard(card);
        given(clientService.findByEmail(authentication.getName())).willReturn(client);

        // Assert
        assertThatThrownBy(() -> underTest.cancelCard("", authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Card number cannot be empty");

        verify(cardRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCardIsNull(){
        // Arrange
        Authentication authentication = mock(Authentication.class);
        Client client = new Client();
        Card card = new Card(CardType.DEBIT, CardColor.GOLD, "Melba Morel", "1010-0020-3853-7754", 123, LocalDate.now(), LocalDate.now().plusYears(5));

        // Act
        client.addCard(card);
        given(clientService.findByEmail(authentication.getName())).willReturn(client);

        // Assert
        assertThatThrownBy(() -> underTest.cancelCard(card.getNumber(), authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Card does not exist.");

        verify(cardRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCardIsNotFromUser(){
        // Arrange
        Authentication authentication = mock(Authentication.class);
        Client client = new Client();
        Card card = new Card(CardType.DEBIT, CardColor.GOLD, "Melba Morel", "1010-0020-3853-7754", 123, LocalDate.now(), LocalDate.now().plusYears(5));
        Card card1 = new Card();

        // Act
        client.addCard(card);
        given(clientService.findByEmail(authentication.getName())).willReturn(client);
        given(underTest.getCardByNumber(card.getNumber())).willReturn(card1);

        // Assert
        assertThatThrownBy(() -> underTest.cancelCard(card.getNumber(), authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("This card does not belong to the current user.");

        verify(cardRepository, never()).save(any());
    }

    @Test
    void shouldAddCardTransaction() {
        // Arrange
        Card card = new Card(CardType.DEBIT, CardColor.GOLD, "Melba Morel", "1010-0020-3853-7754", 123, LocalDate.now(), LocalDate.now().plusYears(5));
        CardTransactionDTO cardTransactionDTO = new CardTransactionDTO("1010-0020-3853-7754", 123, 2000.0, "test");
        Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", "333");
        Account acc1 = new Account("VIN001", LocalDateTime.now(), 50000, AccountType.SAVINGS);
        cli1.addCard(card);
        cli1.addAccount(acc1);

        // Act
        given(underTest.getCardByNumber(cardTransactionDTO.getNumber())).willReturn(card);
        underTest.addCardTransaction(cardTransactionDTO);

        // Assert
        verify(transactionService).save(any());
        verify(accountService).save(acc1);
    }

    @Test
    void shouldThrowWhenThereIsANullField(){
        // Arrange
        CardTransactionDTO cardTransactionDTO = new CardTransactionDTO("", null, null, "");

        // Act
        // Assert
        assertThatThrownBy(()-> underTest.addCardTransaction(cardTransactionDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("⋆ The following fields are empty: amount, cvv, description, card number.");

        verify(transactionService, never()).save(any());
        verify(accountService, never()).save(any());
    }

    @Test
    void shouldThrowWhenCardNumberDoesNotExist(){
        // Arrange
        CardTransactionDTO cardTransactionDTO = new CardTransactionDTO("1010-0020-3853-7754", 123, 2000.0, "test");

        // Act
        given(underTest.getCardByNumber(cardTransactionDTO.getNumber())).willReturn(null);

        // Assert
        assertThatThrownBy(()->underTest.addCardTransaction(cardTransactionDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Not existing card.");

        verify(transactionService, never()).save(any());
        verify(accountService, never()).save(any());
    }

    @Test
    void shouldThrowWhenCvvProvidedNotCorrect(){
        // Arrange
        Card card = new Card(CardType.DEBIT, CardColor.GOLD, "Melba Morel", "1010-0020-3853-7756", 123, LocalDate.now(), LocalDate.now().plusYears(5));
        CardTransactionDTO cardTransactionDTO = new CardTransactionDTO("1010-0020-3853-7754", 124, 2000.0, "test");

        // Act
        given(underTest.getCardByNumber(cardTransactionDTO.getNumber())).willReturn(card);

        // Assert
        assertThatThrownBy(()-> underTest.addCardTransaction(cardTransactionDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("The provided cvv is not correct.");

        verify(accountService, never()).save(any());
        verify(transactionService, never()).save(any());
    }

    @Test
    void shouldThrowWhenCardTypeIsNotValid(){
        // Arrange
        Card card = new Card(CardType.CREDIT, CardColor.GOLD, "Melba Morel", "1010-0020-3853-7754", 123, LocalDate.now(), LocalDate.now().plusYears(5));
        CardTransactionDTO cardTransactionDTO = new CardTransactionDTO("1010-0020-3853-7754", 123, 2000.0, "test");

        // Act
        given(underTest.getCardByNumber(cardTransactionDTO.getNumber())).willReturn(card);

        // Assert
        assertThatThrownBy(()-> underTest.addCardTransaction(cardTransactionDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("The card is not a Debit Card.");

        verify(accountService, never()).save(any());
        verify(transactionService, never()).save(any());
    }



    @Test
    void shouldThrowWhenNotEnoughMoneyInTheAccount(){
        // Arrange
        Card card = new Card(CardType.DEBIT, CardColor.GOLD, "Melba Morel", "1010-0020-3853-7756", 123, LocalDate.now(), LocalDate.now().plusYears(5));
        CardTransactionDTO cardTransactionDTO = new CardTransactionDTO("1010-0020-3853-7754", 123, 2000.0, "test");
        Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", "333");

        // Act
        cli1.addCard(card);
        given(underTest.getCardByNumber(cardTransactionDTO.getNumber())).willReturn(card);

        // Assert
        assertThatThrownBy(()->underTest.addCardTransaction(cardTransactionDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("You do not have enough money in your accounts to complete the transaction.");

        verify(transactionService, never()).save(any());
        verify(accountService, never()).save(any());
    }

    @Test
    void getCardNumber() {
    }

    @Test
    void getCVV() {
    }

    @Test
    public void shouldCreateCardNumber(){
        // Act
        String cardNumber = underTest.getCardNumber();

        // Assert
        assertThat(cardNumber,is(not(emptyOrNullString())));
    }

    @Test
    public void cardNumberCreatedShouldNotBeDuplicated(){
        // Act
        String cardNumber = underTest.getCardNumber();
        boolean existsCardByNumber = underTest.existsCardByNumber(cardNumber);

        // Assert
        assertThat(existsCardByNumber, is(false));
    }

    @Test
    public void cvvShouldBeLowerThan1000(){
        // Act
        int cvv = underTest.getCVV();

        // Assert
        assertThat(cvv, is(lessThan(1000)));
    }

    @Test
    public void cvvShouldBeHigherThan99(){
        // Act
        int cvv = underTest.getCVV();

        // Assert
        assertThat(cvv, is(greaterThan(99)));
    }

}