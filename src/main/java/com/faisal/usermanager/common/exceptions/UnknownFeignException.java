package com.faisal.usermanager.common.exceptions;

import lombok.Getter;

@Getter
public class UnknownFeignException extends RuntimeException {
    private final ErrorMessage errorMessage;

    public UnknownFeignException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }
}
