package com.faisal.usermanager.common.exceptions;

import lombok.Getter;

@Getter
public class InternalServerError extends RuntimeException {
    private final ErrorMessage errorMessage;

    public InternalServerError(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }
}
