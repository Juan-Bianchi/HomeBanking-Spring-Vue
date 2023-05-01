package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface CardRepository  extends JpaRepository<Card, Long> {
    public boolean existsCardByNumber(String number);
    public Card getCardByNumber(String number);
}
