package com.autohire.flow.infrastructure.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * Interface for file storage operations
 */
public interface FileStorageService {
    
    /**
     * Upload file and return storage key
     */
    String uploadFile(MultipartFile file, String userId);
    
    /**
     * Download file content
     */
    byte[] downloadFile(String storageKey);
    
    /**
     * Delete file
     */
    void deleteFile(String storageKey);
    
    /**
     * Check if file exists
     */
    boolean fileExists(String storageKey);
}
