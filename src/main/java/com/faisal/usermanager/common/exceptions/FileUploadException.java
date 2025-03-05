package com.faisal.usermanager.common.exceptions;

import lombok.Getter;

@Getter
public class FileUploadException extends RuntimeException {
    private final ErrorMessage errorMessage;

    public FileUploadException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }
}
