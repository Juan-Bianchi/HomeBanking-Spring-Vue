package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.*;

@SpringBootTest
class CardRepositoryTest {

    @Autowired
    private CardRepository cardRepository;
    private Card card;

    @BeforeEach
    public void arrange(){
        card = new Card(CardType.DEBIT, CardColor.TITANIUM, "Clark Kent", "1070-0020-3843-7754", 968, LocalDate.now(), LocalDate.now().plusYears(5));
        cardRepository.save(card);
    }

    @AfterEach
    public void swipe(){
        cardRepository.deleteAll();
        card = null;
    }

    @Test
    public void shouldExistCardByNumber() {
        //Arrange
        String expected = card.getNumber();
        //Act
        boolean obtained = cardRepository.existsCardByNumber(expected);
        //Assert
        assertThat(obtained,is(true));
    }

    @Test
    public void shouldGetCardByNumber() {
        //Act
        Card obtained = cardRepository.getCardByNumber(card.getNumber());
        //Assert
        assertThat(obtained,is(card));
    }
}