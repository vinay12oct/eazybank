package com.eazybank.account.repository;
import java.util.Optional;
import com.eazybank.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account,Long> {

    List<Account> findByCustomerId(Long customerId);

    Optional<Account> findByAccountNumber(Long accountNumber);

    Optional<Account> findByCustomerIdAndAccountType(Long customerId, String accountType);

    boolean existsByAccountNumber(Long accountNumber);
}
