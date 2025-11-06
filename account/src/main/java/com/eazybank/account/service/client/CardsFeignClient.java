package com.eazybank.account.service.client;


import com.eazybank.account.dto.CardsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="cards",fallback = CardsFallback.class)
public interface CardsFeignClient {

    @GetMapping(value = "/api/v1/card/fetch",consumes = "application/json")
    public ResponseEntity<CardsDto> fetchCardDetails(@RequestHeader("eazyBank-correlation-id") String correlationId, @RequestParam String mobileNumber);
}
