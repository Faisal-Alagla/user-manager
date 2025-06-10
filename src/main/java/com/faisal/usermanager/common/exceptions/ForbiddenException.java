package com.faisal.usermanager.common.exceptions;

import lombok.Getter;

@Getter
public class ForbiddenException extends RuntimeException {
    private final ErrorMessage errorMessage;

    public ForbiddenException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }
}
