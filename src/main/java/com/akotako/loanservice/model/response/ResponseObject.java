package com.akotako.loanservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseObject<T> {
    private boolean isSuccess;
    private T data;
    private String errorMessage;

    public static <T> ResponseObject<T> success(T data) {
        return new ResponseObject<>(true, data, null);
    }

    public static <T> ResponseObject<T> failure(String errorMessage) {
        return new ResponseObject<>(false, null, errorMessage);
    }
}
