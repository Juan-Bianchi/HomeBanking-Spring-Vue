package com.mindhub.homebanking.utils;


import com.mindhub.homebanking.services.AccountService;

public final class AccountUtils {

    static public String createAccountNumber(AccountService accountService){
        String numberAccount = "VIN" + String.format("%08d", (int)(Math.random()*99999999));
        while(accountService.existsAccountByNumber(numberAccount)){
            numberAccount = "VIN" + String.format("%08d", (int)(Math.random()*999999));
        }

        return numberAccount;
    }

}
