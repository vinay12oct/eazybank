package com.eazybank.account.controller;

import com.eazybank.account.dto.AccountContactInfoDto;
import com.eazybank.account.dto.ApiResponse;
import com.eazybank.account.dto.AccountDto;
import com.eazybank.account.service.AccountService;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    private final AccountContactInfoDto accountContactInfoDto;

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    // ✅ Create new account
    @PostMapping()
    public ResponseEntity<ApiResponse<AccountDto>> createAccount(
          @Valid @RequestBody AccountDto accountDto) {
        AccountDto savedAccount = accountService.createAccount(accountDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("201", "Account created successfully", savedAccount));
    }

    // ✅ Get account by account number
    @GetMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<AccountDto>> getAccount(@Valid @PathVariable Long accountNumber) {
        AccountDto account = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(ApiResponse.success("200", "Account fetched successfully", account));
    }

    // ✅ Get all accounts for a customer
    @GetMapping("/customers/{customerId}")
    public ResponseEntity<ApiResponse<List<AccountDto>>> getAccountsByCustomer(@Valid @PathVariable Long customerId) {
        List<AccountDto> accounts = accountService.getAccountsByCustomerId(customerId);
        return ResponseEntity.ok(ApiResponse.success("200", "Accounts fetched successfully", accounts));
    }

    // ✅ Update account
    @PutMapping("/{accountId}")
    public ResponseEntity<ApiResponse<AccountDto>> updateAccount(
            @PathVariable Long accountId,
        @Valid    @RequestBody AccountDto accountDto) {
        AccountDto updatedAccount = accountService.updateAccount(accountId, accountDto);
        return ResponseEntity.ok(ApiResponse.success("200", "Account updated successfully", updatedAccount));
    }

    // ✅ Delete account
    @DeleteMapping("/{accountId}")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@Valid @PathVariable Long accountId) {
        accountService.deleteAccount(accountId);
        return ResponseEntity.ok(ApiResponse.success("200", "Account deleted successfully", null));
    }

    @Retry(name = "getContactInfo",fallbackMethod = "getContactInfoFallback")
    @GetMapping("/contact-info")
    public ResponseEntity<ApiResponse<AccountContactInfoDto>> getContactInfo() {

        logger.debug("get getContactInfo() method invoked");

        throw new NullPointerException();

      //  return ResponseEntity.ok(ApiResponse.success("200", "Accounts fetched successfully", accountContactInfoDto));
    }

    public ResponseEntity<ApiResponse<AccountContactInfoDto>> getContactInfoFallback(Throwable throwable) {

        logger.debug("get getContactInfoFallback() method invoked");
        return ResponseEntity.ok(ApiResponse.success("200", "Accounts not fetched ", null));
    }
}
