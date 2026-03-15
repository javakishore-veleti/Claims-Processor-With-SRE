package com.healthcare.claims.api.claims.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@Slf4j
public class DocumentStorageService {

    private final Path storageDir;

    public DocumentStorageService(
            @Value("${claims.storage.local-path:./claim-documents}") String localPath) {
        this.storageDir = Paths.get(localPath);
        try {
            Files.createDirectories(storageDir);
        } catch (IOException e) {
            log.error("Could not create storage directory: {}", localPath, e);
        }
    }

    public String store(String tenantId, String claimId, MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path tenantDir = storageDir.resolve(tenantId).resolve(claimId);
            Files.createDirectories(tenantDir);
            Path target = tenantDir.resolve(fileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            String storageKey = tenantId + "/" + claimId + "/" + fileName;
            log.info("Stored document: {} ({} bytes)", storageKey, file.getSize());
            return storageKey;
        } catch (IOException e) {
            log.error("Failed to store file: {}", e.getMessage());
            throw new RuntimeException("Failed to store document", e);
        }
    }

    public byte[] retrieve(String storageKey) {
        try {
            Path filePath = storageDir.resolve(storageKey);
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("Failed to retrieve file: {}", storageKey);
            throw new RuntimeException("Document not found", e);
        }
    }

    public void delete(String storageKey) {
        try {
            Path filePath = storageDir.resolve(storageKey);
            Files.deleteIfExists(filePath);
            log.info("Deleted document: {}", storageKey);
        } catch (IOException e) {
            log.warn("Failed to delete file: {}", storageKey);
        }
    }
}
