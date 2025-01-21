package com.faisal.usermanager.common.exceptions;

import lombok.Getter;

@Getter
public enum ErrorMessage {

    //validation
    CONSTRAINT_VIOLATED_EXCEPTION(1_000, "Constraint violated exception"),
    INVALID_REQUEST_PAYLOAD(1_001, "The request content is not valid and could not be deserialized."),
    INVALID_REQUEST_ATTRIBUTES(1_002, "Invalid request attributes"),
    INVALID_PATH(1_003, "Invalid path value"),
    MISSING_REQUEST_PARAMETER(1_004, "Required parameter is missing"),
    UNSUPPORTED_REQUEST_METHOD(1_005, "Request method is not supported"),
    INVALID_API_USAGE(1_007, "Invalid API usage"),
    DATA_INTEGRITY_VIOLATION_EXCEPTION(1_007, "Data integrity violation exception"),

    //user
    USER_NOT_FOUND(2_000, "User not found"),
    CONFLICT_USER_EMAIL_EXISTS(2_001, "User email already exists"),

    //group
    GROUP_NOT_FOUND(2_100, "Group not found"),
    CONFLICT_GROUP_NAME_EXISTS(2_001, "Group name already exists"),

    //lookup

    //activity

    //general
    INTERNAL_SERVER_ERROR(5_000, "Internal Server Error");

    private final Integer internalCode;
    private final String message;

    ErrorMessage(Integer internalCode, String message) {
        this.internalCode = internalCode;
        this.message = message;
    }
}
