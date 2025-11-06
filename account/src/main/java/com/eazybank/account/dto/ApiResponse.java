package com.eazybank.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private String statusCode;
    private String statusMsg;
    private T data;

    // 👇 Extra constructor (only code & msg, no data)
    public ApiResponse(String statusCode, String statusMsg) {
        this.statusCode = statusCode;
        this.statusMsg = statusMsg;
        this.data = null;
    }

    // 👇 Factory method for success with data
    public static <T> ApiResponse<T> success(String code, String msg, T data) {
        return new ApiResponse<>(code, msg, data);
    }

    // 👇 Factory method for success without data
    public static <T> ApiResponse<T> success(String code, String msg) {
        return new ApiResponse<>(code, msg, null);
    }

    // 👇 Factory method for error
    public static <T> ApiResponse<T> error(String code, String msg) {
        return new ApiResponse<>(code, msg, null);
    }
}
