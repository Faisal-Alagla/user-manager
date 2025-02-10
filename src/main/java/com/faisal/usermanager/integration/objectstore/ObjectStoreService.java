package com.faisal.usermanager.integration.objectstore;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.faisal.usermanager.common.exceptions.ErrorMessage;
import com.faisal.usermanager.common.exceptions.ObjectStoreException;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ObjectStoreService {

    private final MinioClient minioClient;

    private final ObjectStoreConfig config;

    public Pair<byte[], String> getFile(String objectName) {
        if (!StringUtils.hasText(objectName)) {
            throw new ObjectStoreException(ErrorMessage.OBJECT_STORE_INVALID_OBJECT_NAME);
        }

        try {
            GetObjectResponse response = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(config.getBucketName())
                            .object(objectName)
                            .build()
            );

            byte[] content = IOUtils.toByteArray(response);
            String contentType = response.headers().get("Content-Type");

            return Pair.of(content, contentType);
        } catch (Exception e) {
            log.error("Failed to get object: {}", objectName, e);
            throw new ObjectStoreException(ErrorMessage.OBJECT_STORE_GET_OBJECT_FAILED);
        }
    }

    public void uploadFile(byte[] fileBuffer, String objectName, String contentType) {
        if (!StringUtils.hasText(objectName)) {
            throw new ObjectStoreException(ErrorMessage.OBJECT_STORE_INVALID_OBJECT_NAME);
        }

        if (fileBuffer == null || fileBuffer.length == 0) {
            throw new ObjectStoreException(ErrorMessage.OBJECT_STORE_INVALID_FILE_CONTENT);
        }

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(config.getBucketName())
                            .object(objectName)
                            .contentType(contentType)
                            .stream(new ByteArrayInputStream(fileBuffer), fileBuffer.length, -1)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to upload object: {}", objectName, e);
            throw new ObjectStoreException(ErrorMessage.OBJECT_STORE_UPLOAD_FAILED);
        }
    }

    public boolean doesObjectExist(String objectName) {
        if (!StringUtils.hasText(objectName)) {
            throw new ObjectStoreException(ErrorMessage.OBJECT_STORE_INVALID_OBJECT_NAME);
        }

        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(config.getBucketName())
                            .object(objectName)
                            .build()
            );
            return true;

        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code()) ||
                    "NoSuchBucket".equals(e.errorResponse().code()) ||
                    "AccessDenied".equals(e.errorResponse().code())) {
                return false;
            }

            log.error("Failed to check object existence: {}", objectName, e);
            throw new ObjectStoreException(ErrorMessage.OBJECT_STORE_STAT_FAILED);
        } catch (Exception e) {

            log.error("Failed to check object existence: {}", objectName, e);
            throw new ObjectStoreException(ErrorMessage.OBJECT_STORE_STAT_FAILED);
        }
    }

    public CompletableFuture<List<String>> deleteFilesByPaths(List<String> filePaths) {
        if (filePaths == null || filePaths.isEmpty()) {
            return CompletableFuture.completedFuture(List.of());
        }

        List<DeleteObject> objects = filePaths.stream()
                .filter(StringUtils::hasText)
                .map(DeleteObject::new)
                .collect(Collectors.toList());

        return CompletableFuture.supplyAsync(() -> {
            List<String> failedDeletes = new ArrayList<>();

            try {
                Iterable<Result<DeleteError>> results = minioClient.removeObjects(
                        RemoveObjectsArgs.builder()
                                .bucket(config.getBucketName())
                                .objects(objects)
                                .build()
                );

                results.forEach(result -> {
                    try {
                        DeleteError error = result.get();
                        failedDeletes.add(error.objectName());
                        log.error("Failed to delete object: {}. Reason: {}",
                                error.objectName(), error.message());
                    } catch (Exception e) {
                        log.error("Error processing delete result", e);
                    }
                });
            } catch (Exception e) {
                log.error("Failed to delete objects", e);
                throw new ObjectStoreException(ErrorMessage.OBJECT_STORE_DELETE_FAILED);
            }

            return failedDeletes;
        });
    }

}