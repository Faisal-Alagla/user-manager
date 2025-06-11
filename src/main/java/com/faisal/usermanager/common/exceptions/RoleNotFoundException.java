package com.faisal.usermanager.common.exceptions;

import lombok.Getter;

@Getter
public class RoleNotFoundException extends RuntimeException {
    private final ErrorMessage errorMessage;

    public RoleNotFoundException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }
}
