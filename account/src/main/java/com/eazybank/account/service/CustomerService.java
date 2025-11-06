package com.eazybank.account.service;

import com.eazybank.account.dto.CustomerDetailsDto;
import com.eazybank.account.dto.CustomerDto;
import java.util.List;

public interface CustomerService {

    // Create a new customer
    CustomerDto createCustomer(CustomerDto customerDto);

    // Fetch customer by ID
    CustomerDto getCustomerById(Long customerId);

    CustomerDto getCustomerByMobileNumber(String mobileNumber);

    // Fetch all customers
    List<CustomerDto> getAllCustomers();

    // Update customer details
    CustomerDto updateCustomer(Long customerId, CustomerDto customerDto);

    // Delete customer by ID
    void deleteCustomer(Long customerId);

    CustomerDetailsDto fetchCustpmerDetails(String mobileNumber,String correlationId);
}
