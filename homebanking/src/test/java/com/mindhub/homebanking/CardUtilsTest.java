package com.mindhub.homebanking;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.utils.CardUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class CardUtilsTest {

    @Autowired
    private CardService cardService;

    @Test
    public void cardNumberIsCreated(){

        String cardNumber = CardUtils.getCardNumber(cardService);
        assertThat(cardNumber,is(not(emptyOrNullString())));
    }

    @Test
    public void cardNumberCreatedIsNotDuplicated(){

        String cardNumber = CardUtils.getCardNumber(cardService);
        boolean existsCardByNumber = cardService.existsCardByNumber(cardNumber);
        assertThat(existsCardByNumber, is(false));
    }

    @Test
    public void CvvIsLowerThan1000(){

        int cvv = CardUtils.getCVV();
        assertThat(cvv, is(lessThan(1000)));
    }

    @Test
    public void CvvIsHigherThan99(){

        int cvv = CardUtils.getCVV();
        assertThat(cvv, is(greaterThan(99)));
    }


}
