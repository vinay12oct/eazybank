package com.eazybank.account.mapper;

import com.eazybank.account.dto.AccountDto;
import com.eazybank.account.entity.Account;

public class AccountMapper {

    // Convert Entity → DTO
    public static AccountDto mapToDto(Account account) {
        if (account == null) {
            return null;
        }
        AccountDto dto = new AccountDto();
        dto.setCustomerId(account.getCustomerId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setAccountType(account.getAccountType());
        dto.setBranchName(account.getBranchName());
        return dto;
    }

    // Convert DTO → Entity
    public static Account mapToEntity(AccountDto dto) {
        if (dto == null) {
            return null;
        }
        Account account = new Account();
        account.setCustomerId(dto.getCustomerId());  // passed explicitly
        account.setAccountNumber(dto.getAccountNumber()); // usually null for new inserts
        account.setAccountType(dto.getAccountType());
        account.setBranchName(dto.getBranchName());
        return account;
    }
}
