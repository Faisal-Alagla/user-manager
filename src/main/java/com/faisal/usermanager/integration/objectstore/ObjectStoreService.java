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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import com.faisal.usermanager.common.exceptions.ErrorMessage;
import com.faisal.usermanager.common.exceptions.ObjectStoreException;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ObjectStoreService implements IObjectStoreService {

    private final MinioClient minioClient;
    private final ObjectStoreConfig config;

    @Override
    public ObjectOperationResult<ObjectMetadata> uploadObject(byte[] objectBuffer, String objectPath, String contentType) {
        validateUploadParameters(objectBuffer, objectPath);

        try {
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(config.getBucketName())
                    .object(objectPath)
                    .contentType(contentType)
                    .stream(new ByteArrayInputStream(objectBuffer), objectBuffer.length, -1)
                    .build();

            minioClient.putObject(args);

            ObjectMetadata metadata = ObjectMetadata.builder()
                    .fileName(objectPath)
                    .contentType(contentType)
                    .size(objectBuffer.length)
                    .path(objectPath)
                    .build();

            return ObjectOperationResult.success(metadata);

        } catch (Exception e) {
            log.error("Failed to upload object: {}", objectPath, e);
            return ObjectOperationResult.error(ObjectStoreErrorCode.UPLOAD_FAILED, e.getMessage());
        }
    }

    @Override
    public ObjectOperationResult<Pair<byte[], String>> getObject(String objectPath) {
        validateGetParameters(objectPath);

        try {
            GetObjectArgs args = GetObjectArgs.builder()
                    .bucket(config.getBucketName())
                    .object(objectPath)
                    .build();

            GetObjectResponse response = minioClient.getObject(args);
            byte[] content = IOUtils.toByteArray(response);
            String contentType = response.headers().get("Content-Type");

            return ObjectOperationResult.success(Pair.of(content, contentType));

        } catch (Exception e) {
            log.error("Failed to get object: {}", objectPath, e);
            return ObjectOperationResult.error(ObjectStoreErrorCode.GET_FAILED, e.getMessage());
        }
    }

    @Override
    public ObjectOperationResult<Boolean> deleteObject(String objectPath) {
        if (!StringUtils.hasText(objectPath)) {
            return ObjectOperationResult.success(false);
        }

        try {
            ObjectOperationResult<List<String>> result = deleteObjects(List.of(objectPath));
            if (!result.isSuccess()) {
                return ObjectOperationResult.error(
                        ObjectStoreErrorCode.valueOf(result.getErrorCode()),
                        result.getErrorMessage()
                );
            }

            List<String> failedDeletes = result.getData();
            boolean deleted = failedDeletes == null || failedDeletes.isEmpty();
            return ObjectOperationResult.success(deleted);

        } catch (Exception e) {
            log.error("Failed to delete object: {}", objectPath, e);
            return ObjectOperationResult.error(ObjectStoreErrorCode.DELETE_FAILED, e.getMessage());
        }
    }

    @Override
    public ObjectOperationResult<List<String>> deleteObjects(List<String> objectPaths) {
        if (CollectionUtils.isEmpty(objectPaths)) {
            return ObjectOperationResult.success(Collections.emptyList());
        }

        try {
            List<DeleteObject> objects = objectPaths.stream()
                    .filter(StringUtils::hasText)
                    .map(DeleteObject::new)
                    .collect(Collectors.toList());

            List<String> failedDeletes = new ArrayList<>();
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(
                    RemoveObjectsArgs.builder()
                            .bucket(config.getBucketName())
                            .objects(objects)
                            .build()
            );

            processDeleteResults(results, failedDeletes);
            return ObjectOperationResult.success(failedDeletes);

        } catch (Exception e) {
            log.error("Batch delete failed", e);
            return ObjectOperationResult.error(ObjectStoreErrorCode.BATCH_DELETE_FAILED, e.getMessage());
        }
    }

    private void validateUploadParameters(byte[] objectBuffer, String objectPath) {
        if (!StringUtils.hasText(objectPath)) {
            throw new ObjectStoreException(ErrorMessage.OBJECT_STORE_INVALID_OBJECT_NAME);
        }

        if (objectBuffer == null || objectBuffer.length == 0) {
            throw new ObjectStoreException(ErrorMessage.OBJECT_STORE_INVALID_FILE_CONTENT);
        }
    }

    private void validateGetParameters(String objectPath) {
        if (!StringUtils.hasText(objectPath)) {
            throw new ObjectStoreException(ErrorMessage.OBJECT_STORE_INVALID_OBJECT_NAME);
        }
    }

    private void processDeleteResults(Iterable<Result<DeleteError>> results, List<String> failedDeletes) {
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
    }

}