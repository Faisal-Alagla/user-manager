package com.faisal.usermanager.integration.objectstore;

import org.springframework.data.util.Pair;

import java.util.List;

public interface IObjectStoreService {

    /**
     * Get an object from object store
     *
     * @param objectPath path/name of the object
     * @return operation result containing pair of object bytes and content type
     */
    ObjectOperationResult<Pair<byte[], String>> getObject(String objectPath);

    /**
     * Upload an object to the object store
     *
     * @param objectBuffer object as a byte array
     * @param objectPath path/name of the object
     * @param contentType content type of the object
     * @return operation result containing object metadata
     */
    ObjectOperationResult<ObjectMetadata> uploadObject(byte[] objectBuffer, String objectPath, String contentType);

    /**
     * Delete a single object from object store
     *
     * @param objectPath path/name of the object to delete
     * @return operation result indicating success or failure
     */
    ObjectOperationResult<Boolean> deleteObject(String objectPath);

    /**
     * Delete multiple objects from object store
     *
     * @param objectPaths list of object paths/names to delete
     * @return operation result containing list of failed deletions
     */
    ObjectOperationResult<List<String>> deleteObjects(List<String> objectPaths);

}