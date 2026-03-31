package com.autohire.flow.infrastructure.storage;

import com.autohire.flow.domain.exception.FileTooLargeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.UUID;

/**
 * Local file storage implementation
 */
@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "local", matchIfMissing = true)
@Slf4j
@RequiredArgsConstructor
public class LocalStorageService implements FileStorageService {
    
    @Value("${storage.local.path:/app/storage}")
    private String basePath;
    
    @Value("${spring.servlet.multipart.max-file-size:5MB}")
    private String maxFileSize;
    
    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5MB
    
    @Override
    public String uploadFile(MultipartFile file, String userId) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }
            
            if (file.getSize() > MAX_FILE_SIZE_BYTES) {
                throw new FileTooLargeException(file.getSize(), MAX_FILE_SIZE_BYTES);
            }
            
            // Create user directory
            String userDir = basePath + File.separator + userId;
            Path userPath = Paths.get(userDir);
            Files.createDirectories(userPath);
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
            
            String uniqueFilename = UUID.randomUUID() + extension;
            Path filePath = userPath.resolve(uniqueFilename);
            
            // Save file
            file.transferTo(filePath);
            
            String storageKey = userId + "/" + uniqueFilename;
            log.info("File uploaded successfully: {}", storageKey);
            
            return storageKey;
            
        } catch (IOException e) {
            log.error("Failed to upload file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
    }
    
    @Override
    public byte[] downloadFile(String storageKey) {
        try {
            Path filePath = Paths.get(basePath, storageKey);
            
            if (!Files.exists(filePath)) {
                throw new IllegalArgumentException("File not found: " + storageKey);
            }
            
            return Files.readAllBytes(filePath);
            
        } catch (IOException e) {
            log.error("Failed to download file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to download file: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteFile(String storageKey) {
        try {
            Path filePath = Paths.get(basePath, storageKey);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("File deleted: {}", storageKey);
            }
            
        } catch (IOException e) {
            log.error("Failed to delete file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete file: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean fileExists(String storageKey) {
        Path filePath = Paths.get(basePath, storageKey);
        return Files.exists(filePath);
    }
}
