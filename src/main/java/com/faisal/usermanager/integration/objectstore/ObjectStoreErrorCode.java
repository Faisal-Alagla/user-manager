package com.faisal.usermanager.integration.objectstore;

import lombok.Getter;

@Getter
enum ObjectStoreErrorCode {

    UPLOAD_FAILED("Failed to upload object to storage"),
    GET_FAILED("Failed to get object from storage"),
    STAT_FAILED("Failed to get object statistics"),
    DELETE_FAILED("Failed to delete object from storage"),
    BATCH_DELETE_FAILED("Failed to delete multiple objects from storage"),
    FILE_READ_ERROR("Failed to read file content");

    private final String message;

    ObjectStoreErrorCode(String message) {
        this.message = message;
    }

}