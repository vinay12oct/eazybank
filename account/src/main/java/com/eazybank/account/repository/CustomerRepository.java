package com.eazybank.account.repository;

import com.eazybank.account.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {

    boolean existsByEmail(String email);
    boolean existsByMobileNumber(String mobileNumber);
    Optional<Customer> getByMobileNumber(String mobileNumber);
    boolean existsByPanNumber(String panNumber);
    boolean existsByAadharNumber(String aadharNumber);


}
