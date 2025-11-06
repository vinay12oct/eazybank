package com.eazybank.gatewayserver.filters;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
@Component
public class FilterUtility {

    public static final String CORRELATION_ID = "eazyBank-correlation-id";

    public String getCorrelationId(HttpHeaders requestHeaders) {
        if (requestHeaders.get(CORRELATION_ID) != null) {
            return requestHeaders.get(CORRELATION_ID).get(0);
        } else {
            return null;
        }
    }

    public void setCorrelationId(ServerWebExchange exchange, String correlationId) {
        exchange.getAttributes().put(CORRELATION_ID, correlationId);
    }

    public String getCorrelationId(ServerWebExchange exchange) {
        return (String) exchange.getAttribute(CORRELATION_ID);
    }
}
