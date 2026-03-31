package com.autohire.flow.application.port.outgoing;

import org.springframework.web.multipart.MultipartFile;

/**
 * Output port for file storage operations.
 * Abstraction for S3, local storage, or other file systems.
 */
public interface FileStoragePort {
    
    /**
     * Stores a file and returns a storage key/URL.
     * @param file file to store
     * @param userId user ID for organizing storage
     * @return storage key or URL for later retrieval
     */
    String store(MultipartFile file, Long userId);
    
    /**
     * Retrieves file contents by storage key.
     * @param storageKey key returned from store()
     * @return byte array of file contents
     */
    byte[] retrieve(String storageKey);
    
    /**
     * Checks if file exists at given key.
     * @param storageKey storage key
     * @return true if file exists, false otherwise
     */
    boolean exists(String storageKey);
    
    /**
     * Deletes file from storage.
     * @param storageKey storage key
     */
    void delete(String storageKey);
}
