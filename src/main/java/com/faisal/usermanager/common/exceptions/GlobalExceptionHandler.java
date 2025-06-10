package com.faisal.usermanager.common.exceptions;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private ErrorResponse createErrorResponse(ErrorMessage message) {
        return ErrorResponse
                .builder()
                .timestamp(new Date())
                .internalCode(message.getInternalCode())
                .message(message.getMessage())
                .build();
    }

    /***
     * Global exception handler for any unhandled exception
     *
     * @param ex .
     * @param request .
     * @return Exception - ErrorResponse with internal server error
     */
    @ExceptionHandler(Exception.class)
    private ResponseEntity<ErrorResponse> handleException(Exception ex, WebRequest request) {
        log.error("in {}: unhandled exception handler", request.getContextPath(), ex);

        ErrorResponse errorResponse = createErrorResponse(ErrorMessage.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Global exception handler for Path resource not found
     *
     * @param ex      .
     * @param request .
     * @return Exception - ErrorResponse with error message
     */
    @ExceptionHandler(NoResourceFoundException.class)
    private ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex, WebRequest request) {
        log.error("in {}: no path resource found", request.getContextPath(), ex);

        ErrorResponse errorResponse = createErrorResponse(ErrorMessage.INVALID_PATH);
        errorResponse.setDescription(ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Global exception handler for Data resources
     *
     * @param ex      .
     * @param request .
     * @return Exception - ErrorResponse with error message
     */
    @ExceptionHandler(ResourceException.class)
    private ResponseEntity<ErrorResponse> handleResourceException(ResourceException ex, WebRequest request) {
        log.error("in {}: resource exception", request.getContextPath(), ex);

        ErrorResponse errorResponse = createErrorResponse(ex.getErrorMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Global exception handler for type mismatch arguments passed to controllers
     *
     * @param ex      .
     * @param request .
     * @return Exception - ErrorResponse with violation place clarification
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    private ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, WebRequest request) {
        log.error("in {}: args mismatch exception", request.getContextPath(), ex);

        ErrorResponse errorResponse = createErrorResponse(ErrorMessage.CONSTRAINT_VIOLATED_EXCEPTION);
        errorResponse.setDescription(
                String.format("%s required %S!",
                        ex.getPropertyName(),
                        Objects.requireNonNull(ex.getRequiredType()).getSimpleName()
                )
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Global exception handler for database constraints violation
     *
     * @param ex      .
     * @param request .
     * @return Exception - ErrorResponse with violation place clarification
     */
    @ExceptionHandler(ConstraintViolationException.class)
    private ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        log.error("in {}: constraint violation", request.getContextPath(), ex);

        ErrorResponse errorResponse = createErrorResponse(ErrorMessage.CONSTRAINT_VIOLATED_EXCEPTION);
        ex.getConstraintViolations().stream().findFirst().ifPresent(constraintViolation ->
                errorResponse.setDescription(constraintViolation.getMessage())
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Global exception handler for when an http request cannot be read
     *
     * @param ex      .
     * @param request .
     * @return Exception - ErrorResponse with invalid request payload
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    private ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        log.error("http message not readable exception handler", ex);

        ErrorResponse errorResponse = createErrorResponse(ErrorMessage.INVALID_REQUEST_PAYLOAD);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Global exception handler for arguments failing validation
     *
     * @param ex      .
     * @param request .
     * @return Exception - ErrorResponse with violation place clarification
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        log.error("method argument exception handler", ex);

        List<String> argumentErrors = ex.getBindingResult().getFieldErrors().stream().map(
                fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage()
        ).toList();

        ErrorResponse errorResponse = createErrorResponse(ErrorMessage.INVALID_REQUEST_ATTRIBUTES);
        errorResponse.setDescription(argumentErrors.toString());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Global exception handler for feign exceptions
     *
     * @param ex      .
     * @param request .
     * @return Exception - ErrorResponse with internal server error
     */
    @ExceptionHandler(UnknownFeignException.class)
    private ResponseEntity<ErrorResponse> handleUnknownFeignException(UnknownFeignException ex, WebRequest request) {
        log.error("unknown feign exception handler", ex);

        ErrorResponse errorResponse = ErrorResponse
                .builder()
                .internalCode(ex.getErrorMessage().getInternalCode())
                .message(ex.getErrorMessage().getMessage())
                .timestamp(new Date())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Global exception handler for resource conflict
     *
     * @param ex
     * @param request
     * @return Exception - ErrorResponse with error message
     */
    @ExceptionHandler(ConflictException.class)
    private ResponseEntity<ErrorResponse> handleConflictException(ConflictException ex, WebRequest request) {
        log.error("conflict exception handler", ex);

        ErrorResponse errorResponse = ErrorResponse
                .builder()
                .internalCode(ex.getErrorMessage().getInternalCode())
                .message(ex.getErrorMessage().getMessage())
                .timestamp(new Date())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Global exception handler for unavailable service exceptions
     *
     * @param ex
     * @param request
     * @return Exception - ErrorResponse with error message
     */
    @ExceptionHandler(ServiceConnectException.class)
    private ResponseEntity<ErrorResponse> handleServiceConnectException(ServiceConnectException ex, WebRequest request) {
        log.error("service connect exception handler", ex);

        ErrorResponse errorResponse = ErrorResponse
                .builder()
                .internalCode(ex.getErrorMessage().getInternalCode())
                .message(ex.getErrorMessage().getMessage())
                .timestamp(new Date())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Global exception handler for requests with invalid data
     *
     * @param ex
     * @param request
     * @return Exception - ErrorResponse with bad request error
     */
    @ExceptionHandler(DataValidationException.class)
    private ResponseEntity<ErrorResponse> handleDataValidationException(DataValidationException ex, WebRequest request) {
        log.error("data validation exception handler", ex);

        List<String> argumentErrors = ex.getFieldErrors().stream().map(
                fieldError -> fieldError.getFirst() + ": " + fieldError.getSecond()
        ).toList();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ErrorMessage.INVALID_REQUEST_ATTRIBUTES.getMessage())
                .internalCode(ErrorMessage.INVALID_REQUEST_ATTRIBUTES.getInternalCode())
                .timestamp(new Date())
                .description(argumentErrors.toString())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Global exception handler for bad requests
     *
     * @param ex
     * @param request
     * @return Exception - ErrorResponse with bad request error
     */
    @ExceptionHandler(BadRequestException.class)
    private ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex, WebRequest request) {
        log.error("bad request exception handler", ex);

        ErrorResponse errorResponse = ErrorResponse
                .builder()
                .internalCode(ex.getErrorMessage().getInternalCode())
                .message(ex.getErrorMessage().getMessage())
                .timestamp(new Date())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Global exception handler for requests with missing required request params
     *
     * @param ex
     * @param request
     * @return Exception - ErrorResponse with error message
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    private ResponseEntity<ErrorResponse> handleMissingRequestParamException(MissingServletRequestParameterException ex, WebRequest request) {
        log.error("missing request parameter exception handler", ex);

        ErrorResponse errorResponse = ErrorResponse
                .builder()
                .internalCode(ErrorMessage.MISSING_REQUEST_PARAMETER.getInternalCode())
                .message(ErrorMessage.MISSING_REQUEST_PARAMETER.getMessage())
                .description("Parameter '" + ex.getParameterName() + "' is required but was not provided in the request.")
                .timestamp(new Date())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Global exception handler for unsupported http method requests
     *
     * @param ex
     * @param request
     * @return Exception - ErrorResponse with error message
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    private ResponseEntity<ErrorResponse> handleRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        log.error("request method not supported exception handler", ex);

        ErrorResponse errorResponse = ErrorResponse
                .builder()
                .internalCode(ErrorMessage.UNSUPPORTED_REQUEST_METHOD.getInternalCode())
                .message(ErrorMessage.UNSUPPORTED_REQUEST_METHOD.getMessage())
                .description("Method '" + ex.getMethod() + "' is not supported.")
                .timestamp(new Date())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Global exception handler for invalid api usage
     *
     * @param ex
     * @param request
     * @return Exception - ErrorResponse with error message
     */
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    private ResponseEntity<ErrorResponse> handleRequestMethodNotSupportedException(InvalidDataAccessApiUsageException ex, WebRequest request) {
        log.error("request method not supported exception handler", ex);

        ErrorResponse errorResponse = ErrorResponse
                .builder()
                .internalCode(ErrorMessage.INVALID_API_USAGE.getInternalCode())
                .message(ErrorMessage.INVALID_API_USAGE.getMessage())
                .timestamp(new Date())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Global exception handler for internal server errors
     *
     * @param ex
     * @param request
     * @return Exception - ErrorResponse with internal server error
     */
    @ExceptionHandler(InternalServerError.class)
    private ResponseEntity<ErrorResponse> handleInternalServerError(InternalServerError ex, WebRequest request) {
        log.error("internal server exception handler", ex);

        ErrorResponse errorResponse = ErrorResponse
                .builder()
                .internalCode(ex.getErrorMessage().getInternalCode())
                .message(ex.getErrorMessage().getMessage())
                .timestamp(new Date())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Global exception handler for service timeouts
     *
     * @param ex
     * @param request
     * @return Exception - ErrorResponse with error message
     */
    @ExceptionHandler(ServiceTimeoutException.class)
    private ResponseEntity<ErrorResponse> handleServiceTimeoutError(ServiceTimeoutException ex, WebRequest request) {
        log.error("service timeout exception handler", ex);

        ErrorResponse errorResponse = ErrorResponse
                .builder()
                .internalCode(ex.getErrorMessage().getInternalCode())
                .message(ex.getErrorMessage().getMessage())
                .timestamp(new Date())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.REQUEST_TIMEOUT);
    }

    /**
     * Global exception handler for data integrity violations
     *
     * @param ex
     * @param request
     * @return Exception - ErrorResponse with error message
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    private ResponseEntity<ErrorResponse> handleServiceTimeoutError(DataIntegrityViolationException ex, WebRequest request) {
        log.error("service timeout exception handler", ex);

        ErrorResponse errorResponse = createErrorResponse(ErrorMessage.DATA_INTEGRITY_VIOLATION_EXCEPTION);
        errorResponse.setDescription(extractConstraintViolationMessage(ex.getMessage()));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private String extractConstraintViolationMessage(String rawMessage) {
        if (rawMessage == null) {
            return "Data integrity violation occurred.";
        }

        if (rawMessage.contains("violates foreign key constraint")) {
            return "A foreign key constraint was violated.";
        } else if (rawMessage.contains("violates unique constraint")) {
            return "A unique constraint was violated.";
        } else if (rawMessage.contains("null value in column")) {
            return "A null value was provided for a column that does not allow nulls.";
        } else {
            return "A data integrity constraint was violated.";
        }
    }

    /**
     * Global exception handler for file upload exceptions
     *
     * @param ex
     * @param request
     * @return Exception - ErrorResponse with error message
     */
    @ExceptionHandler(FileUploadException.class)
    private ResponseEntity<ErrorResponse> handleFileUploadException(FileUploadException ex, WebRequest request) {
        log.error("file upload exception handler", ex);

        ErrorResponse errorResponse = ErrorResponse
                .builder()
                .internalCode(ex.getErrorMessage().getInternalCode())
                .message(ex.getErrorMessage().getMessage())
                .timestamp(new Date())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Global exception handler for Spring's max upload size exceeded
     *
     * @param ex
     * @param request
     * @return Exception - ErrorResponse with error message
     */
    @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
    private ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(
            org.springframework.web.multipart.MaxUploadSizeExceededException ex, WebRequest request) {
        log.error("max upload size exceeded exception handler", ex);

        ErrorResponse errorResponse = ErrorResponse
                .builder()
                .internalCode(ErrorMessage.FILE_SIZE_EXCEEDED.getInternalCode())
                .message(ErrorMessage.FILE_SIZE_EXCEEDED.getMessage())
                .description("The file size exceeds the maximum allowed upload size.")
                .timestamp(new Date())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Global exception handler for media type not supported
     *
     * @param ex
     * @param request
     * @return Exception - ErrorResponse with error message
     */
    @ExceptionHandler(org.springframework.web.HttpMediaTypeNotSupportedException.class)
    private ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(
            org.springframework.web.HttpMediaTypeNotSupportedException ex, WebRequest request) {
        log.error("media type not supported exception handler", ex);

        ErrorResponse errorResponse = ErrorResponse
                .builder()
                .internalCode(ErrorMessage.UNSUPPORTED_FILE_TYPE.getInternalCode())
                .message(ErrorMessage.UNSUPPORTED_FILE_TYPE.getMessage())
                .description("The content type '" + ex.getContentType() + "' is not supported.")
                .timestamp(new Date())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * Global exception handler for multipart exceptions
     *
     * @param ex
     * @param request
     * @return Exception - ErrorResponse with error message
     */
    @ExceptionHandler(org.springframework.web.multipart.MultipartException.class)
    private ResponseEntity<ErrorResponse> handleMultipartException(
            org.springframework.web.multipart.MultipartException ex, WebRequest request) {
        log.error("multipart exception handler", ex);

        ErrorResponse errorResponse = ErrorResponse
                .builder()
                .internalCode(ErrorMessage.FILE_UPLOAD_ERROR.getInternalCode())
                .message(ErrorMessage.FILE_UPLOAD_ERROR.getMessage())
                .description("Failed to process the multipart request: " + ex.getMessage())
                .timestamp(new Date())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Global exception handler for forbidden actions
     *
     * @param ex      .
     * @param request .
     * @return Exception - ErrorResponse with forbidden error
     */
    @ExceptionHandler(ForbiddenException.class)
    private ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException ex, WebRequest request) {
        log.error("forbidden exception handler", ex);

        ErrorResponse errorResponse = ErrorResponse
                .builder()
                .internalCode(ex.getErrorMessage().getInternalCode())
                .message(ex.getErrorMessage().getMessage())
                .timestamp(new Date())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

}
