package com.eazybank.account.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AccountDto {

    @NotNull(message = "Customer ID is required")
    @Min(value = 1, message = "Customer ID must be greater than 0")
    private Long customerId;

    @Min(value = 1000000000L, message = "Account number must be at least 10 digits")
    private Long accountNumber;

    @NotBlank(message = "Account type is required")
    @Pattern(regexp = "^(SAVINGS|CURRENT|FIXED|LOAN)$",
            message = "Account type must be one of: SAVINGS, CURRENT, FIXED, LOAN")
    private String accountType;

    @NotBlank(message = "Branch name is required")
    @Pattern(regexp = "^[A-Za-z ]{3,100}$",
            message = "Branch name must only contain alphabets and spaces, length 3–100")
    private String branchName;
}
