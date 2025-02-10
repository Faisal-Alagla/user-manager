package com.faisal.usermanager.integration.objectstore;

import io.minio.MinioClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;
import com.faisal.usermanager.common.exceptions.ErrorMessage;
import com.faisal.usermanager.common.exceptions.ObjectStoreException;

@Slf4j
@Getter
@Setter
@Configuration
@Validated
@ConfigurationProperties(prefix = "user-manager.object-store")
public class ObjectStoreConfig {

    @NotBlank
    private String url;

    @NotBlank
    private String accessKey;

    @NotBlank
    private String accessSecret;

    @NotBlank
    private String bucketName;

    @Bean
    public MinioClient minioClient() {
        try {
            return MinioClient.builder()
                    .endpoint(url)
                    .credentials(accessKey, accessSecret)
                    .build();
        } catch (Exception e) {
            log.error("Failed to create MinIO client", e);
            throw new ObjectStoreException(ErrorMessage.OBJECT_STORE_INIT_FAILED);
        }
    }
}