package com.faisal.usermanager.common.exceptions;

import lombok.Getter;

@Getter
public class ServiceTimeoutException extends RuntimeException {
    private final ErrorMessage errorMessage;

    public ServiceTimeoutException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }
}
