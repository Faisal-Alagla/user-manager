package com.faisal.usermanager.common.exceptions;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final ErrorMessage errorMessage;

    public UserNotFoundException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }
}
