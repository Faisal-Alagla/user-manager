package com.faisal.usermanager.integration.objectstore;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ObjectOperationResult<T> {

    private final boolean success;
    private final T data;
    private final String errorMessage;
    private final String errorCode;

    public static <T> ObjectOperationResult<T> success(T data) {
        return ObjectOperationResult.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ObjectOperationResult<T> error(ObjectStoreErrorCode errorCode, String message) {
        return ObjectOperationResult.<T>builder()
                .success(false)
                .errorCode(errorCode.name())
                .errorMessage(message != null ? message : errorCode.getMessage())
                .build();
    }
}