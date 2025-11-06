package com.eazybank.account.service.client;

import com.eazybank.account.dto.LoansDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class LoansFallback implements LoansFeignClient {

    @Override
    public ResponseEntity<LoansDto> fetchLoanDetails(String correlationId, String mobileNumber) {
        // Create a safe fallback response
        LoansDto fallbackLoan = new LoansDto();
        fallbackLoan.setLoanNumber("0000000000");
        fallbackLoan.setLoanType("N/A");
        fallbackLoan.setTotalLoan(0);
        fallbackLoan.setAmountPaid(0);
        fallbackLoan.setOutstandingAmount(0);
        fallbackLoan.setMobileNumber(mobileNumber);

        return new ResponseEntity<>(fallbackLoan, HttpStatus.OK);
    }
}
