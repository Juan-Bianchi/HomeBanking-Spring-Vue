package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientLoanDTO;
import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanCreationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.services.*;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/api")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService){
        this.loanService = loanService;
    }

    @GetMapping("/loans")
    public Set<LoanDTO> getLoansList(){
        return loanService.findAll().stream().map(LoanDTO::new).collect(toSet());
    }

    @PostMapping("/loans")
    @Transactional
    public ResponseEntity<Object> createClientLoan(@RequestBody LoanApplicationDTO loanApplicationDTO, Authentication authentication){
        try{
            return new ResponseEntity<>(loanService.createClientLoan(loanApplicationDTO, authentication), HttpStatus.CREATED);
        }
        catch (RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/genericLoans")
    public ResponseEntity<Object> setGenericLoan(@RequestBody LoanCreationDTO genericLoan, Authentication authentication){
        try{
            loanService.setGenericLoan(genericLoan, authentication);
            return new ResponseEntity<>("Loan created.", HttpStatus.OK);
        }
        catch (RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }

    }


}
