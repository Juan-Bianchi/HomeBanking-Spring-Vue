package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mindhub.homebanking.models.TransactionType.CREDIT;
import static com.mindhub.homebanking.models.TransactionType.DEBIT;

@SpringBootApplication
public class HomebankingApplication {

	@Autowired
	private PasswordEncoder passwordEncoder;
	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}


	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, LoanRepository loanRepository, ClientLoanRepository clientLoanRepository, CardRepository cardRepository) {
		return (args) -> {

			String passwordCli1 = passwordEncoder.encode("melba555");
			Client cli1 = new Client("Melba", "Morel", "melba@mindhub.com", passwordCli1);
			Account acc1 = new Account("VIN001", LocalDateTime.now(), 50000);
			Account acc2 = new Account("VIN002", LocalDateTime.now().plusDays(1), 75000);
			Account acc5 = new Account("VIN005", LocalDateTime.now().plusDays(2), 20000);
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


			Loan ln1 = new Loan("Mortgage", 500000, Arrays.asList(6,12,24,36,48,60));
			Loan ln2 = new Loan("Personal", 100000, Arrays.asList(6,12,24));
			Loan ln3 = new Loan("Automotive", 300000, Arrays.asList(6,12,24,36));

			ClientLoan clLoan1 = new ClientLoan(cli1, ln1, 400000, 60);
			ClientLoan clLoan2 = new ClientLoan(cli1, ln2, 50000, 12);

			Card crd1 = new Card(CardType.DEBIT, CardColor.GOLD, "Melba Morel", "1111-2222-3333-4444", 123, LocalDate.now(), LocalDate.now().plusYears(5));
			Card crd2 = new Card(CardType.CREDIT, CardColor.TITANIUM, "Melba Morel","1110-0022-3343-4454", 333, LocalDate.now(), LocalDate.now().plusYears(5));
			Card crd4 = new Card(CardType.DEBIT, CardColor.TITANIUM, "Melba Morel", "1010-0020-3843-7754", 968, LocalDate.now(), LocalDate.now().plusYears(5));
			Card crd5 = new Card(CardType.CREDIT, CardColor.SILVER, "Melba Morel", "1770-0020-9343-1454", 252, LocalDate.now(), LocalDate.now().plusYears(5));
			Card crd6 = new Card(CardType.DEBIT, CardColor.SILVER, "Melba Morel", "1180-0012-4343-4354", 140, LocalDate.now(), LocalDate.now().plusYears(5));
			Card crd7 = new Card(CardType.CREDIT, CardColor.GOLD, "Melba Morel",  "1360-1022-3743-4754", 320, LocalDate.now(), LocalDate.now().plusYears(5));


			cli1.addAccount(acc1);
			cli1.addAccount(acc2);
			/*cli1.addAccount(acc5);*/
			cli1.addClientLoan(clLoan1);
			cli1.addClientLoan(clLoan2);
			cli1.addCard(crd1);
			cli1.addCard(crd2);
			cli1.addCard(crd4);
			cli1.addCard(crd5);
			cli1.addCard(crd6);
			/*cli1.addCard(crd7);*/
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
			ln1.addClientLoan(clLoan1);
			ln2.addClientLoan(clLoan2);

			loanRepository.save(ln1);
			loanRepository.save(ln2);
			loanRepository.save(ln3);

			clientRepository.save(cli1);
			accountRepository.save(acc1);
			accountRepository.save(acc2);
			accountRepository.save(acc5);
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
			clientLoanRepository.save(clLoan1);
			clientLoanRepository.save(clLoan2);
			cardRepository.save(crd1);
			cardRepository.save(crd2);
			cardRepository.save(crd4);
			cardRepository.save(crd5);
			cardRepository.save(crd6);
			//cardRepository.save(crd7);



			String passwordCli2 = passwordEncoder.encode("juan555");
			Client cli2 = new Client("Juan", "Bianchi", "mail@mail.com", passwordCli2);
			Account acc3 = new Account("VIN003", LocalDateTime.now(), 50000);
			Account acc4 = new Account("VIN004", LocalDateTime.now().plusDays(1), 75000);
			ClientLoan clLoan3 = new ClientLoan(cli2, ln2, 100000, 24);
			ClientLoan clLoan4 = new ClientLoan(cli2, ln3, 200000, 36);
			Card crd3 = new Card(CardType.CREDIT, CardColor.SILVER, "Juan Bianchi", "9999-8888-7777-6666", 987, LocalDate.now(), LocalDate.now().plusYears(5));

			cli2.addAccount(acc3);
			cli2.addAccount(acc4);
			cli2.addClientLoan(clLoan3);
			cli2.addClientLoan(clLoan4);
			cli2.addCard(crd3);
			ln2.addClientLoan(clLoan3);
			ln3.addClientLoan(clLoan4);



			clientRepository.save(cli2);
			accountRepository.save(acc3);
			accountRepository.save(acc4);
			clientLoanRepository.save(clLoan3);
			clientLoanRepository.save(clLoan4);
			cardRepository.save(crd3);













		};

	}
}
