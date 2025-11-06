package com.eazybank.account.service.client;

import com.eazybank.account.dto.CardsDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CardsFallback implements CardsFeignClient {

    @Override
    public ResponseEntity<CardsDto> fetchCardDetails(String correlationId, String mobileNumber) {
        // Create a safe fallback response
        CardsDto fallbackCard = new CardsDto();
        fallbackCard.setCardNumber("0000-0000-0000-0000");
        fallbackCard.setCardType("N/A");
        fallbackCard.setTotalLimit(0);
        fallbackCard.setAmountUsed(0);
        fallbackCard.setAvailableAmount(0);
        fallbackCard.setMobileNumber(mobileNumber);

        return new ResponseEntity<>(fallbackCard, HttpStatus.OK);
    }
}
