package com.autohire.flow.infrastructure.storage;

import com.autohire.flow.domain.exception.FileTooLargeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.UUID;

/**
 * AWS S3 file storage implementation
 */
@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "s3")
@Slf4j
@RequiredArgsConstructor
public class S3StorageService implements FileStorageService {
    
    private final S3Client s3Client;
    
    @Value("${aws.s3.bucket-name:autohire-resumes}")
    private String bucketName;
    
    @Value("${storage.s3.region:us-east-1}")
    private String region;
    
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
            
            // Generate unique object key
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
            
            String uniqueFilename = UUID.randomUUID() + extension;
            String objectKey = String.format("resumes/%s/%s", userId, uniqueFilename);
            
            // Upload to S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .metadata(java.util.Map.of(
                    "original-filename", originalFilename != null ? originalFilename : "unknown",
                    "upload-time", String.valueOf(System.currentTimeMillis())
                ))
                .build();
            
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            
            log.info("File uploaded to S3: {}", objectKey);
            return objectKey;
            
        } catch (IOException e) {
            log.error("Failed to upload file to S3: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        } catch (S3Exception e) {
            log.error("S3 error during upload: {}", e.getMessage(), e);
            throw new RuntimeException("S3 upload failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public byte[] downloadFile(String storageKey) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(storageKey)
                .build();
            
            return s3Client.getObject(getObjectRequest).readAllBytes();
            
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                throw new IllegalArgumentException("File not found: " + storageKey);
            }
            log.error("S3 error during download: {}", e.getMessage(), e);
            throw new RuntimeException("S3 download failed: " + e.getMessage(), e);
        } catch (IOException e) {
            log.error("Failed to download file from S3: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to download file: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteFile(String storageKey) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(storageKey)
                .build();
            
            s3Client.deleteObject(deleteObjectRequest);
            log.info("File deleted from S3: {}", storageKey);
            
        } catch (S3Exception e) {
            log.error("S3 error during delete: {}", e.getMessage(), e);
            throw new RuntimeException("S3 delete failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean fileExists(String storageKey) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(storageKey)
                .build();
            
            s3Client.headObject(headObjectRequest);
            return true;
            
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            log.warn("S3 error checking file existence: {}", e.getMessage());
            return false;
        }
    }
}
