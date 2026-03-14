package com.healthcare.claims.common.crypto;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * No-op implementation of {@link EncryptionService} that simply prefixes IDs
 * with the {@link IdType} prefix instead of encrypting them.
 * <p>
 * Active when {@code claims.crypto.enabled} is {@code false} (the default).
 * </p>
 */
@Service
@ConditionalOnProperty(name = "claims.crypto.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpEncryptionService implements EncryptionService {

    private static final String SEPARATOR = "_";

    @Override
    public String encrypt(String plainId, IdType idType) {
        if (plainId == null) {
            return null;
        }
        return idType.getPrefix() + SEPARATOR + plainId;
    }

    @Override
    public String decrypt(String encryptedId, IdType idType) {
        if (encryptedId == null) {
            return null;
        }
        String prefix = idType.getPrefix() + SEPARATOR;
        if (encryptedId.startsWith(prefix)) {
            return encryptedId.substring(prefix.length());
        }
        // If no prefix found, return as-is (graceful fallback)
        return encryptedId;
    }

    @Override
    public String encryptObject(Object id, IdType idType) {
        if (id == null) {
            return null;
        }
        return encrypt(id.toString(), idType);
    }

    @Override
    public String decryptToString(String encryptedId, IdType idType) {
        return decrypt(encryptedId, idType);
    }
}
