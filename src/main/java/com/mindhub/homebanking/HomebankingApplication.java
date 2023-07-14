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
import java.util.Arrays;

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
			Account acc1 = new Account("VIN001", LocalDateTime.now(), 50000, AccountType.SAVINGS);
			Account acc2 = new Account("VIN002", LocalDateTime.now().plusDays(1), 75000, AccountType.CURRENT);

			Transaction tr1 = new Transaction(CREDIT, 1000, "TransferVIN002", LocalDateTime.now().plusDays(-3));
			Transaction tr2 = new Transaction(DEBIT, -1000, "TransferVIN001", LocalDateTime.now().plusDays(-3));
			acc1.addTransaction(tr1);
			acc2.addTransaction(tr2);
			acc1.setBalance(acc1.getBalance() + tr1.getAmount());
			acc2.setBalance(acc2.getBalance() + tr1.getAmount());

			Transaction tr3 = new Transaction(CREDIT, 800, "Transfer", LocalDateTime.now().plusDays(-4));
			Transaction tr4 = new Transaction(DEBIT, -800, "Transfer", LocalDateTime.now().plusDays(-4));
			acc1.addTransaction(tr4);
			acc2.addTransaction(tr3);
			acc1.setBalance(acc1.getBalance() + tr4.getAmount());
			acc2.setBalance(acc2.getBalance() + tr3.getAmount());

			Transaction tr5 = new Transaction(CREDIT, 4000, "Transfer", LocalDateTime.now().plusDays(-24));
			Transaction tr6 = new Transaction(DEBIT, -4000, "Transfer", LocalDateTime.now().plusDays(-24));
			acc1.addTransaction(tr6);
			acc2.addTransaction(tr5);
			acc1.setBalance(acc1.getBalance() + tr5.getAmount());
			acc2.setBalance(acc2.getBalance() + tr6.getAmount());

			Transaction tr7 = new Transaction(CREDIT, 1020, "Transfer", LocalDateTime.now().plusDays(-25));
			Transaction tr8 = new Transaction(DEBIT, -1020, "Transfer", LocalDateTime.now().plusDays(-25));
			acc1.addTransaction(tr7);
			acc2.addTransaction(tr8);
			acc1.setBalance(acc1.getBalance() + tr7.getAmount());
			acc2.setBalance(acc2.getBalance() + tr8.getAmount());

			Transaction tr9 = new Transaction(CREDIT, 1050, "Transfer", LocalDateTime.now().plusDays(-28));
			Transaction tr10 = new Transaction(DEBIT, -1050, "Transfer", LocalDateTime.now().plusDays(-28));
			acc1.addTransaction(tr9);
			acc2.addTransaction(tr10);
			acc1.setBalance(acc1.getBalance() + tr9.getAmount());
			acc2.setBalance(acc2.getBalance() + tr10.getAmount());

			Transaction tr11 = new Transaction(CREDIT, 1000, "Transfer", LocalDateTime.now().plusDays(64));
			Transaction tr12 = new Transaction(DEBIT, -1000, "Transfer", LocalDateTime.now().plusDays(-64));
			acc1.addTransaction(tr11);
			acc2.addTransaction(tr12);
			acc1.setBalance(acc1.getBalance() + tr11.getAmount());
			acc2.setBalance(acc2.getBalance() + tr12.getAmount());

			Transaction tr13 = new Transaction(CREDIT, 1000, "Transfer", LocalDateTime.now().plusDays(-64));
			Transaction tr14 = new Transaction(DEBIT, -1000, "Transfer", LocalDateTime.now().plusDays(-64));
			acc1.addTransaction(tr13);
			acc2.addTransaction(tr14);
			acc1.setBalance(acc1.getBalance() + tr13.getAmount());
			acc2.setBalance(acc2.getBalance() + tr14.getAmount());


			Loan ln1 = new Loan("Mortgage", 500000, Arrays.asList(6,12,24,36,48,60), 8.0);
			Loan ln2 = new Loan("Personal", 100000, Arrays.asList(6,12,24), 24.0);
			Loan ln3 = new Loan("Automotive", 300000, Arrays.asList(6,12,24,36), 20.0);

			ClientLoan clLoan1 = new ClientLoan(cli1, ln1, 400000, 60);
			ClientLoan clLoan2 = new ClientLoan(cli1, ln2, 50000, 12);

			Card crd1 = new Card(CardType.DEBIT, CardColor.GOLD, "Melba Morel", "1010-0020-3853-7754", 123, LocalDate.now(), LocalDate.now().plusYears(5));
			Card crd2 = new Card(CardType.CREDIT, CardColor.TITANIUM, "Melba Morel","1010-0820-3843-7754", 333, LocalDate.now(), LocalDate.now().plusYears(5));

			Card crd4 = new Card(CardType.DEBIT, CardColor.TITANIUM, "Melba Morel", "1010-0020-3843-7754", 968, LocalDate.now(), LocalDate.now().plusYears(5));
			Card crd5 = new Card(CardType.CREDIT, CardColor.SILVER, "Melba Morel", "1770-0020-9343-1454", 252, LocalDate.now().minusYears(5), LocalDate.now().plusDays(15));
			Card crd6 = new Card(CardType.DEBIT, CardColor.SILVER, "Melba Morel", "1180-0012-4343-4354", 140, LocalDate.now().minusMonths(11).minusYears(4), LocalDate.now().minusMonths(1));


			cli1.addAccount(acc1);
			cli1.addAccount(acc2);
			cli1.addClientLoan(clLoan1);
			cli1.addClientLoan(clLoan2);
			cli1.addCard(crd1);
			cli1.addCard(crd2);
			cli1.addCard(crd4);
			cli1.addCard(crd5);
			cli1.addCard(crd6);
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

			String passwordAdmin = passwordEncoder.encode("admin");
			Client admin = new Client("admin", "admin", "admin@mindhub.com", passwordAdmin);

			clientRepository.save(admin);

			String passwordCli2 = passwordEncoder.encode("juan555");
			Client cli2 = new Client("Juan", "Bianchi", "mail@mail.com", passwordCli2);
			Account acc3 = new Account("VIN003", LocalDateTime.now(), 50000, AccountType.CURRENT);
			Account acc4 = new Account("VIN004", LocalDateTime.now().plusDays(1), 75000, AccountType.SAVINGS);
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
