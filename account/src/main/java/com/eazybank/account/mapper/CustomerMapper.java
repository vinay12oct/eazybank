package com.eazybank.account.mapper;

import com.eazybank.account.dto.CustomerDto;
import com.eazybank.account.entity.Customer;

public class CustomerMapper {

    // Entity → DTO
    public static CustomerDto mapToDto(Customer customer) {
        if (customer == null) {
            return null;
        }
        CustomerDto dto = new CustomerDto();
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setMobileNumber(customer.getMobileNumber());
        dto.setPanNumber(customer.getPanNumber());
        dto.setAadharNumber(customer.getAadharNumber());
        return dto;
    }

    // DTO → Entity
    public static Customer mapToEntity(CustomerDto dto) {
        if (dto == null) {
            return null;
        }
        Customer customer = new Customer();
        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setMobileNumber(dto.getMobileNumber());
        customer.setPanNumber(dto.getPanNumber());
        customer.setAadharNumber(dto.getAadharNumber());
        return customer;
    }
}
