package com.eazybank.account.service.impl;

import com.eazybank.account.dto.AccountDto;
import com.eazybank.account.entity.Account;
import com.eazybank.account.entity.Customer;
import com.eazybank.account.exception.InvalidRequestException;
import com.eazybank.account.exception.ResourceNotFoundException;
import com.eazybank.account.mapper.AccountMapper;
import com.eazybank.account.repository.AccountRepository;
import com.eazybank.account.repository.CustomerRepository;
import com.eazybank.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {


    private final AccountRepository accountRepository;

    private final CustomerRepository customerRepository;

    @Override
    public AccountDto createAccount(AccountDto accountDto) {
        // 1. Validate input
        if (accountDto == null) {
            throw new InvalidRequestException("Account details are required");
        }
        if (accountDto.getCustomerId() == null) {
            throw new InvalidRequestException("Customer ID is required to create an account");
        }

        if (accountDto.getAccountNumber() != null) {
            throw new InvalidRequestException("Account number should not be provided, it will be auto-generated");
        }



        if (accountDto.getAccountNumber() == null) {
            accountDto.setAccountNumber(generateAccountNumber());
        }

        // 🔍 Check if customer exists
        Customer customer = customerRepository.findById(accountDto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with ID: " + accountDto.getCustomerId()));

        // Ensure account number is unique
        if (accountRepository.existsByAccountNumber(accountDto.getAccountNumber())) {
            throw new InvalidRequestException("Account number already exists: " + accountDto.getAccountNumber());
        }

        // 🔍 Validate account number
        if (accountDto.getAccountNumber() == null || accountDto.getAccountNumber() < 1000000000L) {
            throw new InvalidRequestException("Account number must be at least 10 digits");
        }

        // 🔍 Validate account type
        if (accountDto.getAccountType() == null ||
                !accountDto.getAccountType().matches("^(SAVINGS|CURRENT|FIXED|LOAN)$")) {
            throw new InvalidRequestException("Account type must be one of: SAVINGS, CURRENT, FIXED, LOAN");
        }

        // 🔍 Validate branch name
        if (accountDto.getBranchName() == null ||
                !accountDto.getBranchName().matches("^[A-Za-z ]{3,100}$")) {
            throw new InvalidRequestException("Branch name must only contain alphabets and spaces (3–100 chars)");
        }

        // 🚫 Prevent duplicate account type per customer
        Optional<Account> existingAccount = accountRepository
                .findByCustomerIdAndAccountType(accountDto.getCustomerId(), accountDto.getAccountType());
        if (existingAccount.isPresent()) {
            throw new InvalidRequestException("Customer already has a " + accountDto.getAccountType() + " account");
        }

        // Map DTO → Entity
        Account account = AccountMapper.mapToEntity(accountDto);

        // Set metadata fields

        account.setStatus("ACTIVE");
        account.setCreatedAt(LocalDateTime.now());
        account.setCreatedBy("SYSTEM"); // replace with logged-in user later


        // Save Account
        Account saved = accountRepository.save(account);

        // Return DTO
        return AccountMapper.mapToDto(saved);
    }



    @Override
    public AccountDto getAccountByNumber(Long accountNumber) {
        try {
            if (accountNumber == null) {
                throw new InvalidRequestException("Account number cannot be null");
            }

            Account account = accountRepository.findByAccountNumber(accountNumber)
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found for number: " + accountNumber));

            return AccountMapper.mapToDto(account);

        } catch (Exception ex) {
            log.error("Error while fetching account: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public List<AccountDto> getAccountsByCustomerId(Long customerId) {
        try {
            if (customerId == null) {
                throw new InvalidRequestException("CustomerId cannot be null");
            }

            List<Account> accounts = accountRepository.findByCustomerId(customerId);

            return accounts.stream()
                    .map(AccountMapper::mapToDto)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            log.error("Error while fetching accounts for customerId {}: {}", customerId, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public AccountDto updateAccount(Long accountId, AccountDto accountDto) {
        // 🔍 Check if account exists
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + accountId));

        // ❌ Do not allow changing customerId or accountNumber
        if (accountDto.getCustomerId() != null && !accountDto.getCustomerId().equals(account.getCustomerId())) {
            throw new InvalidRequestException("Customer ID cannot be changed");
        }
        if (accountDto.getAccountNumber() != null && !accountDto.getAccountNumber().equals(account.getAccountNumber())) {
            throw new InvalidRequestException("Account number cannot be changed");
        }

        Optional<Account> existingAccount = accountRepository
                .findByCustomerIdAndAccountType(accountDto.getCustomerId(), accountDto.getAccountType());
        if (existingAccount.isPresent()) {
            throw new InvalidRequestException("Customer already has a " + accountDto.getAccountType() + " account");
        }


        // ✅ Allow updates to mutable fields
        if (accountDto.getAccountType() != null) {
            if (!accountDto.getAccountType().matches("^(SAVINGS|CURRENT|FIXED|LOAN)$")) {
                throw new InvalidRequestException("Account type must be one of: SAVINGS, CURRENT, FIXED, LOAN");
            }
            account.setAccountType(accountDto.getAccountType());
        }

        if (accountDto.getBranchName() != null) {
            if (!accountDto.getBranchName().matches("^[A-Za-z ]{3,100}$")) {
                throw new InvalidRequestException("Branch name must only contain alphabets and spaces (3–100 chars)");
            }
            account.setBranchName(accountDto.getBranchName());
        }

        // Update metadata
        account.setUpdatedAt(LocalDateTime.now());
        account.setUpdatedBy("SYSTEM"); // replace with logged-in user later

        Account saved = accountRepository.save(account);
        return AccountMapper.mapToDto(saved);
    }


    @Override
    public void deleteAccount(Long accountId) {
        try {
            if (accountId == null) {
                throw new InvalidRequestException("Account number cannot be null");
            }

            Account existing = accountRepository.findById(accountId)
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found for number: " + accountId));

            existing.setStatus("IN ACTIVE");

            accountRepository.save(existing);

        } catch (Exception ex) {
            log.error("Error while deleting account {}: {}", accountId, ex.getMessage(), ex);
            throw ex;
        }
    }

    private static Long generateAccountNumber() {
        return 1000000000L + new Random().nextLong(9000000000L); // 10-digit unique number
    }
}
