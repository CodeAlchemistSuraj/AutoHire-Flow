package com.autohire.flow.domain.exception;

public class FileTooLargeException extends DomainException {
    
    public FileTooLargeException(long fileSize, long maxSize) {
        super(String.format("File size %d exceeds maximum allowed size %d", fileSize, maxSize), 
              "FILE_TOO_LARGE");
    }
}
