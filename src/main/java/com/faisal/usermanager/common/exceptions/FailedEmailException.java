package com.faisal.usermanager.common.exceptions;

import lombok.Getter;

@Getter
public class FailedEmailException extends RuntimeException {
    private final ErrorMessage errorMessage;

    public FailedEmailException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }
}
