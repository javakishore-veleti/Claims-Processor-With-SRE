package com.healthcare.claims.common.apps.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadInfo {

    private String fileName;
    private String contentType;
    private long fileSize;
    private String storageKey;
    private String uploadedAt;
}
