package com.eazybank.gatewayserver.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Order(1)
@Component
public class RequestTraceFilter implements GlobalFilter {

    @Autowired
    private FilterUtility filterUtility;

    private static final Logger logger = LoggerFactory.getLogger(RequestTraceFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders requestHeaders = exchange.getRequest().getHeaders();

        String correlationId;
        if (isCorrelationIdPresent(requestHeaders)) {
            correlationId = filterUtility.getCorrelationId(requestHeaders);
            logger.debug("eazyBank-correlation-id found in RequestTraceFilter : {}", correlationId);
        } else {
            correlationId = generateCorrelationId();
            filterUtility.setCorrelationId(exchange, correlationId);
            logger.debug("eazyBank-correlation-id generated in RequestTraceFilter : {}", correlationId);
        }

        // ✅ Add correlation ID to outgoing request headers
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(exchange.getRequest()
                        .mutate()
                        .header("eazyBank-correlation-id", correlationId)
                        .build())
                .build();

        return chain.filter(mutatedExchange);
    }




    private boolean isCorrelationIdPresent(HttpHeaders requestHeaders){

        if(filterUtility.getCorrelationId(requestHeaders)!=null){
            return true;
        }else{
            return false;
        }

    }

    private String generateCorrelationId(){
      return java.util.UUID.randomUUID().toString();
    }
}
