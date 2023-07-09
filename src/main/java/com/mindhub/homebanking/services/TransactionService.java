package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.TransactionDTO;
import com.mindhub.homebanking.models.Transaction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    void save(Transaction transaction);
    void postTransactions(@RequestParam(required=false) Double amount, @RequestParam String description,
                                            @RequestParam String origAccountNumb, @RequestParam String destAccountNumb,
                                            Authentication authentication);
    List<TransactionDTO> getTransactions(@RequestParam String accountNumber, @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate, @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime thruDate);
}
