package com.mindhub.homebanking.services.implementations;

import com.mindhub.homebanking.models.ClientLoan;
import com.mindhub.homebanking.repositories.ClientLoanRepository;
import com.mindhub.homebanking.services.ClientLoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientLoanServiceImplementation implements ClientLoanService {

    private final ClientLoanRepository clientLoanRepository;

    public ClientLoanServiceImplementation(ClientLoanRepository clientLoanRepository){
        this.clientLoanRepository = clientLoanRepository;
    }

    @Override
    public void save(ClientLoan clientLoan) {
        clientLoanRepository.save(clientLoan);
    }
}
