package com.autohire.flow.infrastructure.storage.service;

import com.autohire.flow.application.port.outgoing.FileStoragePort;
import com.autohire.flow.common.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Local File Storage Service
 * 
 * Implements FileStoragePort interface for local filesystem storage.
 * Used in development; production should use S3StorageService.
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "file.storage.type", havingValue = "local", matchIfMissing = true)
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStoragePort {

    private final ValidationUtil validationUtil;

    @Value("${file.storage.local.path:./uploads}")
    private String storagePath;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    @Override
    public String store(MultipartFile file, Long userId) {
        log.info("Storing file: {} for user: {}", file.getOriginalFilename(), userId);
        
        // Validate inputs
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        if (!validationUtil.isSafeFileName(file.getOriginalFilename())) {
            throw new IllegalArgumentException("Invalid file name: " + file.getOriginalFilename());
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed (5MB)");
        }
        
        try {
            // Create directory structure for user
            String userDir = "user_" + userId;
            Path dirPath = Paths.get(storagePath, userDir);
            Files.createDirectories(dirPath);
            
            // Generate unique file name
            String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = dirPath.resolve(uniqueFileName);
            
            // Write file
            Files.write(filePath, file.getBytes());
            
            String storageKey = userDir + "/" + uniqueFileName;
            log.info("File stored successfully at: {}", storageKey);
            
            return storageKey;
            
        } catch (IOException e) {
            log.error("Failed to store file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] retrieve(String filePath) {
        log.info("Retrieving file: {}", filePath);
        
        try {
            Path path = Paths.get(storagePath, filePath);
            
            // Security check - ensure path is within storage directory
            if (!path.normalize().startsWith(Paths.get(storagePath).normalize())) {
                throw new SecurityException("Path traversal attempt detected");
            }
            
            if (!Files.exists(path)) {
                throw new IllegalArgumentException("File not found: " + filePath);
            }
            
            byte[] content = Files.readAllBytes(path);
            log.info("File retrieved successfully, size: {} bytes", content.length);
            
            return content;
            
        } catch (IOException e) {
            log.error("Failed to retrieve file: {}", filePath, e);
            throw new RuntimeException("Failed to retrieve file: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean exists(String filePath) {
        try {
            Path path = Paths.get(storagePath, filePath);
            
            // Security check
            if (!path.normalize().startsWith(Paths.get(storagePath).normalize())) {
                return false;
            }
            
            boolean exists = Files.exists(path);
            log.debug("File exists check for {}: {}", filePath, exists);
            return exists;
            
        } catch (Exception e) {
            log.warn("Error checking file existence: {}", filePath, e);
            return false;
        }
    }

    @Override
    public void delete(String filePath) {
        log.info("Deleting file: {}", filePath);
        
        try {
            Path path = Paths.get(storagePath, filePath);
            
            // Security check
            if (!path.normalize().startsWith(Paths.get(storagePath).normalize())) {
                throw new SecurityException("Path traversal attempt detected");
            }
            
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("File deleted successfully: {}", filePath);
            } else {
                log.warn("File not found for deletion: {}", filePath);
            }
            
        } catch (IOException e) {
            log.error("Failed to delete file: {}", filePath, e);
            throw new RuntimeException("Failed to delete file: " + e.getMessage(), e);
        }
    }
}