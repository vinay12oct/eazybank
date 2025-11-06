package com.eazybank.account.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "account")
@Data
public class AccountContactInfoDto {

    private  String message;
    private  Map<String,String> contactDetails;
    private  List<String> onCallSupport;
}
