package com.eazybank.account.controller;

import com.eazybank.account.dto.ApiResponse;
import com.eazybank.account.dto.CustomerDetailsDto;
import com.eazybank.account.dto.CustomerDto;
import com.eazybank.account.service.CustomerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);


    // ✅ Create new customer
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerDto>> createCustomer(@Valid @RequestBody CustomerDto customerDto) {
        CustomerDto savedCustomer = customerService.createCustomer(customerDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("201", "Customer created successfully", savedCustomer));
    }

    // ✅ Get customer by ID
    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerDto>> getCustomer(@Valid @PathVariable Long customerId) {
        CustomerDto customer = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(ApiResponse.success("200", "Customer fetched successfully", customer));
    }

    // ✅ Get all customers
    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerDto>>> getAllCustomers() {
        List<CustomerDto> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(ApiResponse.success("200", "Customers fetched successfully", customers));
    }

    // ✅ Update customer
    @PutMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerDto>> updateCustomer(
            @PathVariable Long customerId,
          @Valid  @RequestBody CustomerDto customerDto) {
        CustomerDto updatedCustomer = customerService.updateCustomer(customerId, customerDto);
        return ResponseEntity.ok(ApiResponse.success("200", "Customer updated successfully", updatedCustomer));
    }

    // ✅ Delete customer
    @DeleteMapping("/{customerId}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@Valid @PathVariable Long customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.ok(ApiResponse.success("200", "Customer deleted successfully", null));
    }

    @GetMapping("/fetchcustomerdetails")

    public ResponseEntity<ApiResponse<CustomerDetailsDto>> fetchCustomerDetails(@Valid @RequestHeader("eazyBank-correlation-id") String correlationId,
                                                                                @RequestParam
                                                                                @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
                                                                                String mobileNumber) {
         logger.debug("fetch customer details start");
        CustomerDetailsDto customerDetailsDto = customerService.fetchCustpmerDetails(mobileNumber,correlationId);
        logger.debug("fetch customer details end");

        return ResponseEntity.ok(ApiResponse.success("200", "Customers details fetched successfully", customerDetailsDto));

    }

    @GetMapping("/hello")
    public ResponseEntity<ApiResponse<String>> getHelloMsg(){
        return  ResponseEntity.ok(ApiResponse.success("200","get new updated  hello msg","hello"));
    }
}
