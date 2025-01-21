package com.faisal.usermanager.common.exceptions;

import lombok.Getter;

@Getter
public class ResourceException extends RuntimeException {
    private final ErrorMessage errorMessage;

    public ResourceException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }
}
