package com.eazybank.account.service.impl;

import com.eazybank.account.dto.*;
import com.eazybank.account.entity.Account;
import com.eazybank.account.entity.Customer;
import com.eazybank.account.exception.*;
import com.eazybank.account.mapper.CustomerMapper;
import com.eazybank.account.repository.AccountRepository;
import com.eazybank.account.repository.CustomerRepository;
import com.eazybank.account.service.AccountService;
import com.eazybank.account.service.CustomerService;
import com.eazybank.account.service.client.CardsFeignClient;
import com.eazybank.account.service.client.LoansFeignClient;
import jakarta.ws.rs.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final CardsFeignClient cardsFeignClient;
    private final LoansFeignClient loansFeignClient;
    private final AccountService accountService;


    @Override
    public CustomerDto createCustomer(CustomerDto customerDto) {
        try {
            if (customerDto == null || customerDto.getName() == null) {
                throw new InvalidRequestException("Customer name is required");
            }

            if (customerRepository.existsByEmail(customerDto.getEmail())) {
                throw new EmailAlreadyExistsException("Email already exists: " + customerDto.getEmail());
            }

            if (customerRepository.existsByMobileNumber(customerDto.getMobileNumber())) {
                throw new MobileAlreadyExistsException("Mobile number already exists: " + customerDto.getMobileNumber());
            }

            if (customerRepository.existsByPanNumber(customerDto.getPanNumber())) {
                throw new CustomerAlreadyExistsException("Customer already exists with PAN: " + customerDto.getPanNumber());
            }

            if (customerRepository.existsByAadharNumber(customerDto.getAadharNumber())) {
                throw new CustomerAlreadyExistsException("Customer already exists with Aadhar: " + customerDto.getAadharNumber());
            }

            Customer customer = CustomerMapper.mapToEntity(customerDto);
            customer.setCreatedBy("SYSTEM");
            Customer saved = customerRepository.save(customer);
            return CustomerMapper.mapToDto(saved);

        } catch (Exception ex) {
            log.error("Error while creating customer: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public CustomerDto getCustomerById(Long customerId) {
        try {
            if (customerId == null) {
                throw new InvalidRequestException("CustomerId cannot be null");
            }

            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

            return CustomerMapper.mapToDto(customer);

        } catch (Exception ex) {
            log.error("Error while fetching customer {}: {}", customerId, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public CustomerDto getCustomerByMobileNumber(String mobileNumber) {


        try {
            if (mobileNumber == null) {
                throw new InvalidRequestException("CustomerId cannot be null");
            }

            Customer customer = customerRepository.getByMobileNumber(mobileNumber)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + mobileNumber));

            return CustomerMapper.mapToDto(customer);

        } catch (Exception ex) {
            log.error("Error while fetching customer {}: {}", mobileNumber, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public List<CustomerDto> getAllCustomers() {
        try {
            return customerRepository.findAll().stream()
                    .map(CustomerMapper::mapToDto)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            log.error("Error while fetching all customers: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public CustomerDto updateCustomer(Long customerId, CustomerDto customerDto) {
        try {
            if (customerId == null) {
                throw new InvalidRequestException("CustomerId cannot be null");
            }

            Customer existing = customerRepository.findById(customerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

            existing.setName(customerDto.getName());
            existing.setEmail(customerDto.getEmail());
            existing.setMobileNumber(customerDto.getMobileNumber());
            existing.setPanNumber(customerDto.getPanNumber());
            existing.setAadharNumber(customerDto.getAadharNumber());

            Customer updated = customerRepository.save(existing);
            return CustomerMapper.mapToDto(updated);

        } catch (Exception ex) {
            log.error("Error while updating customer {}: {}", customerId, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public void deleteCustomer(Long customerId) {
        try {
            if (customerId == null) {
                throw new InvalidRequestException("CustomerId cannot be null");
            }

            Customer existing = customerRepository.findById(customerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

            existing.setStatus("NOT ACTIVE");

            customerRepository.save(existing);

        } catch (Exception ex) {
            log.error("Error while deleting customer {}: {}", customerId, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public CustomerDetailsDto fetchCustpmerDetails(String mobileNumber,String correlationId) {
        if (mobileNumber == null || mobileNumber.trim().isEmpty()) {
            throw new InvalidRequestException("Mobile number cannot be null or empty");
        }

        try {
            // Fetch customer by mobile number
            Customer customer = customerRepository.getByMobileNumber(mobileNumber)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Customer not found with mobile number: " + mobileNumber));

            // Fetch accounts for the customer
            List<AccountDto> accountDtoList = accountService.getAccountsByCustomerId(customer.getCustomerId());

            // Fetch cards (handle null response gracefully)
            CardsDto cardsDto = null;
            try {
                ResponseEntity<CardsDto> response = cardsFeignClient.fetchCardDetails(correlationId,mobileNumber);
                if (response != null && response.getStatusCode().is2xxSuccessful()) {
                    cardsDto = response.getBody();
                }
            } catch (Exception e) {
                log.warn("Unable to fetch card details for customer {}: {}", mobileNumber, e.getMessage());
            }

            // Fetch loans (handle null response gracefully)
            LoansDto loansDto = null;
            try {
                ResponseEntity<LoansDto> response = loansFeignClient.fetchLoanDetails(correlationId,mobileNumber);
                if (response != null && response.getStatusCode().is2xxSuccessful()) {
                    loansDto = response.getBody();
                }
            } catch (Exception e) {
                log.warn("Unable to fetch loan details for customer {}: {}", mobileNumber, e.getMessage());
            }

            // Build response
            CustomerDetailsDto customerDetailsDto = new CustomerDetailsDto();
            customerDetailsDto.setCustomerDto(CustomerMapper.mapToDto(customer));
            customerDetailsDto.setAccountDtos(accountDtoList);
            customerDetailsDto.setCardsDto(cardsDto);
            customerDetailsDto.setLoansDto(loansDto);

            return customerDetailsDto;

        } catch (InvalidRequestException | ResourceNotFoundException ex) {
            log.error("Validation error while fetching customer {}: {}", mobileNumber, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error while fetching customer {}: {}", mobileNumber, ex.getMessage(), ex);
            throw new ServiceUnavailableException("Unable to fetch customer details at the moment, please try again later.");
        }
    }

}
