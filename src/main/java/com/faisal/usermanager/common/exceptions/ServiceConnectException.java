package com.faisal.usermanager.common.exceptions;

import lombok.Getter;

import javax.naming.ServiceUnavailableException;

@Getter
public class ServiceConnectException extends ServiceUnavailableException {
    private final ErrorMessage errorMessage;

    public ServiceConnectException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }
}
