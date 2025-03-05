package com.faisal.usermanager.utils.validations;

import com.faisal.usermanager.utils.FileUtils;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ImageTypeValidator implements ConstraintValidator<ImageTypeValidation, MultipartFile> {

    @Value("${user-manager.max-file-size-mb:5}")
    private long maxFileSizeMB;

    @Value("${user-manager.allowed-image-types:image/jpeg,image/png}")
    private String allowedContentTypesConfig;

    @Value("${user-manager.allowed-image-extensions:jpg,jpeg,png}")
    private String allowedExtensionsConfig;

    private List<String> ALLOWED_IMAGE_CONTENT_TYPES;
    private String[] ALLOWED_EXTENSIONS;

    @PostConstruct
    public void init() {
        ALLOWED_IMAGE_CONTENT_TYPES = Arrays.asList(allowedContentTypesConfig.split(","));
        ALLOWED_EXTENSIONS = allowedExtensionsConfig.split(",");
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true;
        }

        long maxSizeBytes = maxFileSizeMB * 1024 * 1024; // Convert MB to bytes
        if (file.getSize() > maxSizeBytes) {
            customizeErrorMessage(context, "File size exceeds the maximum allowed size of " + maxFileSizeMB + " MB");
            return false;
        }

        String contentType = file.getContentType();
        if (contentType != null && !contentType.startsWith("image/")) {
            customizeErrorMessage(context, "Only image files are allowed. Uploaded file type was: " + contentType);
            return false;
        }

        boolean isValidContentType = ALLOWED_IMAGE_CONTENT_TYPES.contains(contentType);

        boolean isValidExtension = FileUtils.hasValidExtension(file.getOriginalFilename(), ALLOWED_EXTENSIONS);

        boolean isValidMagicNumber;
        try {
            isValidMagicNumber = FileUtils.isValidImageFormat(file);
        } catch (IOException e) {
            customizeErrorMessage(context, "Failed to read file content: " + e.getMessage());
            return false;
        }

        boolean isValid = isValidContentType && isValidExtension && isValidMagicNumber;

        if (!isValid) {
            customizeErrorMessage(context, "Image type must be of (" + String.join("/", ALLOWED_EXTENSIONS) + ")");
        }

        return isValid;
    }

    /**
     * Updates the validation context with a custom error message
     */
    private void customizeErrorMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }

}
