package com.faisal.usermanager.integration.objectstore;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ObjectMetadata {

    private final String fileName;

    private final String contentType;

    private final long size;

    private final String path;

}
