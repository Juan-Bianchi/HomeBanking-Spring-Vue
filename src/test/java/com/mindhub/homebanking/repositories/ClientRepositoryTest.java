package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.Client;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void shouldFindClientByEmail() {
        //Arrange
        Client expected = new Client("Juan", "Bianchi", "mailJuan@mail.com", "12345");
        clientRepository.save(expected);
        //Act
        Client obtained = clientRepository.findByEmail(expected.getEmail());
        //Assert
        assertThat(expected, equalTo(obtained));
        //Annihilate
        clientRepository.delete(expected);
        expected = null;
        obtained = null;
    }
}