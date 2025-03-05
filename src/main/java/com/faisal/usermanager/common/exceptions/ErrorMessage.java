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
    INVALID_API_USAGE(1_006, "Invalid API usage"),
    DATA_INTEGRITY_VIOLATION_EXCEPTION(1_007, "Data integrity violation exception"),

    //user
    USER_NOT_FOUND(2_000, "User not found"),
    CONFLICT_USER_EMAIL_EXISTS(2_001, "User email already exists"),

    //group
    GROUP_NOT_FOUND(2_100, "Group not found"),
    CONFLICT_GROUP_NAME_EXISTS(2_001, "Group name already exists"),

    //lookup
    GROUP_VISIBILITY_NOT_FOUND(3_000, "Group visibility not found"),
    USER_GROUP_ROLE_NOT_FOUND(3_001, "User group role not found"),

    //activity

    //object store
    OBJECT_STORE_UPLOAD_FAILED(4_100, "Failed to upload to object store"),
    OBJECT_STORE_GET_OBJECT_FAILED(4_101, "Failed to get object from object store"),
    OBJECT_STORE_DELETE_FAILED(4_102, "Failed to delete object from object store"),
    OBJECT_STORE_INIT_FAILED(4_103, "Failed to initialize object store client"),
    OBJECT_STORE_STAT_FAILED(4_104, "Failed to get object statistics"),
    OBJECT_STORE_INVALID_OBJECT_NAME(4_105, "Invalid object name provided"),
    OBJECT_STORE_INVALID_FILE_CONTENT(4_106, "Invalid file content provided"),

    //file upload
    FILE_SIZE_EXCEEDED(4_200, "Maximum allowed file size exceeded"),
    FILE_UPLOAD_ERROR(4_202, "File upload error"),
    UNSUPPORTED_FILE_TYPE(4_201, "Unsupported file type"),

    //general
    INTERNAL_SERVER_ERROR(5_000, "Internal Server Error");

    private final Integer internalCode;
    private final String message;

    ErrorMessage(Integer internalCode, String message) {
        this.internalCode = internalCode;
        this.message = message;
    }
}
