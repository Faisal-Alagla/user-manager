package com.faisal.usermanager.common.exceptions;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
class ErrorResponse {
    private Date timestamp;
    private Integer internalCode;
    private String message;
    private String description;
}
