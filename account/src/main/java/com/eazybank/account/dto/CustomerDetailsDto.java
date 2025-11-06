package com.eazybank.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CustomerDetailsDto {

    private CustomerDto customerDto;

    private List<AccountDto> accountDtos;

    private LoansDto loansDto;

    private CardsDto cardsDto;
}
