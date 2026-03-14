package com.healthcare.claims.common.crypto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "claims.crypto")
public class EncryptionProperties {

    private boolean enabled = false;

    private String masterKey = "claims-processor-default-key-32ch";

    private String algorithm = "AES/GCM/NoPadding";
}
