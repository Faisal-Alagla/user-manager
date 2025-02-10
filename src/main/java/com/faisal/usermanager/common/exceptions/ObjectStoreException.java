package com.faisal.usermanager.common.exceptions;

import lombok.Getter;

@Getter
public class ObjectStoreException extends RuntimeException {
    private final ErrorMessage errorMessage;

    public ObjectStoreException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }
}