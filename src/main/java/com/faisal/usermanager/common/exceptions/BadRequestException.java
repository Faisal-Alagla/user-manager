package com.faisal.usermanager.common.exceptions;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
    private final ErrorMessage errorMessage;

    public BadRequestException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }
}
