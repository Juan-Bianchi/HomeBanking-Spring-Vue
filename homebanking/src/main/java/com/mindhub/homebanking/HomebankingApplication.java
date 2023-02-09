package com.mindhub.homebanking;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

import static com.mindhub.homebanking.models.TransactionType.CREDIT;
import static com.mindhub.homebanking.models.TransactionType.DEBIT;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository) {
		return (args) -> {
			Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com");
			Account acc1 = new Account("VIN001", LocalDateTime.now(), 50000);
			Account acc2 = new Account("VIN002", LocalDateTime.now().plusDays(1), 75000);
			Transaction tr1 = new Transaction(CREDIT, 1000, "Transfer", LocalDateTime.now().plusDays(-3));
			Transaction tr2 = new Transaction(DEBIT, -800, "Purchase", LocalDateTime.now().plusDays(-3));
			Transaction tr3 = new Transaction(CREDIT, 100, "Transfer", LocalDateTime.now().plusDays(-4));
			Transaction tr4 = new Transaction(DEBIT, -800, "Purchase", LocalDateTime.now().plusDays(-5));
			Transaction tr5 = new Transaction(CREDIT, 10000, "Transfer", LocalDateTime.now().plusDays(-10));
			Transaction tr6 = new Transaction(DEBIT, -4000, "Purchase", LocalDateTime.now().plusDays(-24));
			Transaction tr7 = new Transaction(CREDIT, 1020, "Transfer", LocalDateTime.now().plusDays(-25));
			Transaction tr8 = new Transaction(DEBIT, -850, "Purchase", LocalDateTime.now().plusDays(-25));
			Transaction tr9 = new Transaction(CREDIT, 1050, "Transfer", LocalDateTime.now().plusDays(-26));
			Transaction tr10 = new Transaction(DEBIT, -8000, "Purchase", LocalDateTime.now().plusDays(-28));
			Transaction tr11 = new Transaction(CREDIT, 1000, "Transfer", LocalDateTime.now().plusDays(-30));
			Transaction tr12 = new Transaction(DEBIT, -4300, "Purchase", LocalDateTime.now().plusDays(-64));
			Transaction tr13 = new Transaction(CREDIT, 1000, "Transfer", LocalDateTime.now().plusDays(-43));
			Transaction tr14 = new Transaction(DEBIT, -4300, "Purchase", LocalDateTime.now().plusDays(-64));

			cli1.addAccount(acc1);
			cli1.addAccount(acc2);
			acc1.addTransaction(tr1);
			acc1.addTransaction(tr2);
			acc1.addTransaction(tr3);
			acc1.addTransaction(tr4);
			acc1.addTransaction(tr5);
			acc1.addTransaction(tr6);
			acc1.addTransaction(tr7);
			acc1.addTransaction(tr8);
			acc1.addTransaction(tr9);
			acc1.addTransaction(tr10);
			acc1.addTransaction(tr11);
			acc1.addTransaction(tr12);
			acc2.addTransaction(tr13);
			acc2.addTransaction(tr14);
			clientRepository.save(cli1);
			accountRepository.save(acc1);
			accountRepository.save(acc2);
			transactionRepository.save(tr1);
			transactionRepository.save(tr2);
			transactionRepository.save(tr3);
			transactionRepository.save(tr4);
			transactionRepository.save(tr5);
			transactionRepository.save(tr6);
			transactionRepository.save(tr7);
			transactionRepository.save(tr8);
			transactionRepository.save(tr9);
			transactionRepository.save(tr10);
			transactionRepository.save(tr11);
			transactionRepository.save(tr12);
			transactionRepository.save(tr13);
			transactionRepository.save(tr14);

			Client cli2 = new Client("Juan", "Bianchi", "mail@mail.com");
			Account acc3 = new Account("VIN003", LocalDateTime.now(), 50000);
			Account acc4 = new Account("VIN004", LocalDateTime.now().plusDays(1), 75000);
			cli2.addAccount(acc3);
			cli2.addAccount(acc4);
			clientRepository.save(cli2);
			accountRepository.save(acc3);
			accountRepository.save(acc4);











		};

	}
}
