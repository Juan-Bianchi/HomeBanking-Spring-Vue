package com.mindhub.homebanking;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository) {
		return (args) -> {
			Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com");
			Account acc1 = new Account("VIN001", LocalDateTime.now(), 5000);
			Account acc2 = new Account("VIN002", LocalDateTime.now().plusDays(1), 7500);
			cli1.addAccount(acc1);
			cli1.addAccount(acc2);
			clientRepository.save(cli1);
			accountRepository.save(acc1);
			accountRepository.save(acc2);

			Client cli2 = new Client("Juan", "Bianchi", "mail@mail.com");
			clientRepository.save(cli2);


		};

	}
}
