package com.faisal.usermanager.integration.objectstore;

import com.faisal.usermanager.common.exceptions.ErrorMessage;
import com.faisal.usermanager.common.exceptions.ObjectStoreException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ObjectStoreInitializer implements InitializingBean {

    private final ObjectStoreService objectStoreService;

    @Value("${user-manager.object-store.max-retries}")
    private int maxRetries = 5;

    @Value("${user-manager.object-store.retry-delay-ms}")
    private long retryDelayMs = 2000;

    @Value("${user-manager.object-store.bucket-name}")
    private String bucketName;

    @Override
    public void afterPropertiesSet() {
        log.info("Initializing object store buckets...");
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                objectStoreService.createBucketIfNotExists(bucketName);
                return;
            } catch (Exception e) {
                retryCount++;
                log.warn("Attempt {} to create bucket failed. Will retry in {} ms", retryCount, retryDelayMs);
                try {
                    Thread.sleep(retryDelayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new ObjectStoreException(ErrorMessage.OBJECT_STORE_BUCKET_CREATION_FAILED);
                }
            }
        }

        log.error("Failed to initialize bucket");
        throw new ObjectStoreException(ErrorMessage.OBJECT_STORE_BUCKET_CREATION_FAILED);
    }
}
