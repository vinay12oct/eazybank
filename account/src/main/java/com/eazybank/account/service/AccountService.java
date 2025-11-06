package com.eazybank.account.service;

import com.eazybank.account.dto.AccountDto;
import java.util.List;

public interface AccountService {

    // Create a new account
    AccountDto createAccount(AccountDto accountDto);

    // Fetch account by account number
    AccountDto getAccountByNumber(Long accountNumber);

    // Fetch all accounts for a customer
    List<AccountDto> getAccountsByCustomerId(Long customerId);

    // Update account details
    AccountDto updateAccount(Long accountNumber, AccountDto accountDto);

    // Delete account by account number
    void deleteAccount(Long accountNumber);
}
