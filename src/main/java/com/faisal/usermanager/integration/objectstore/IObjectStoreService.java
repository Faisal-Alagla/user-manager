package com.faisal.usermanager.integration.objectstore;

import org.springframework.data.util.Pair;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IObjectStoreService {

    /**
     * Get an object from object store by its path/name
     *
     * @param objectName path/name of the object
     * @return Pair of the object as an array of bytes (first), and its content type (second)
     */
    Pair<byte[], String> getObject(String objectName);

    /**
     * upload an object to the object store
     *
     * @param objectBuffer The object as an array of bytes
     * @param objectName The path/name of the object
     * @param contentType The content type of the object
     */
    void uploadObject(byte[] objectBuffer, String objectName, String contentType);

    /**
     * Checks whether an object exists by its path/name
     *
     * @param objectName path/name of the object
     * @return true of the object exists, false otherwise
     */
    boolean doesObjectExist(String objectName);

    /**
     * Delete multiple objects from object store by their paths/names
     *
     * @param objectPaths list of paths/names of the objects to delete
     * @return CompletableFuture containing list of paths that failed to delete
     */
    CompletableFuture<List<String>> deleteObjectsByPaths(List<String> objectPaths);

}
