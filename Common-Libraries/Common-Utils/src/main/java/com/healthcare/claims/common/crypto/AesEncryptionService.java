package com.healthcare.claims.common.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * AES-256-GCM based encryption service.
 * <p>
 * Each {@link IdType} gets its own derived key from the master key + IdType prefix as salt,
 * ensuring that identical plain-text IDs of different types produce different ciphertexts.
 * </p>
 * <p>
 * The output format is URL-safe Base64 (no padding) of: {@code [12-byte IV][ciphertext+tag]}.
 * </p>
 * This class is thread-safe; a {@link SecureRandom} instance is used for IV generation
 * and per-IdType derived keys are cached in a {@link ConcurrentHashMap}.
 */
@Service
@ConditionalOnProperty(name = "claims.crypto.enabled", havingValue = "true", matchIfMissing = false)
public class AesEncryptionService implements EncryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;   // 96 bits
    private static final int GCM_TAG_LENGTH = 128;  // bits
    private static final String KEY_ALGORITHM = "AES";

    private final SecureRandom secureRandom = new SecureRandom();
    private final ConcurrentMap<IdType, SecretKey> derivedKeys = new ConcurrentHashMap<>();
    private final String masterKey;

    public AesEncryptionService(
            @Value("${claims.crypto.master-key:claims-processor-default-key-32ch}") String masterKey) {
        this.masterKey = masterKey;
    }

    @Override
    public String encrypt(String plainId, IdType idType) {
        if (plainId == null || plainId.isBlank()) {
            throw new CryptoException("Plain ID must not be null or blank");
        }
        try {
            SecretKey key = deriveKey(idType);
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

            byte[] ciphertext = cipher.doFinal(plainId.getBytes(StandardCharsets.UTF_8));

            // Prepend IV to ciphertext: [IV][ciphertext+tag]
            byte[] combined = ByteBuffer.allocate(iv.length + ciphertext.length)
                    .put(iv)
                    .put(ciphertext)
                    .array();

            return Base64.getUrlEncoder().withoutPadding().encodeToString(combined);
        } catch (CryptoException e) {
            throw e;
        } catch (Exception e) {
            throw new CryptoException("Encryption failed for IdType " + idType.name(), e);
        }
    }

    @Override
    public String decrypt(String encryptedId, IdType idType) {
        if (encryptedId == null || encryptedId.isBlank()) {
            throw new CryptoException("Encrypted ID must not be null or blank");
        }
        try {
            byte[] combined = Base64.getUrlDecoder().decode(encryptedId);
            if (combined.length < GCM_IV_LENGTH) {
                throw new CryptoException("Invalid encrypted data: too short");
            }

            byte[] iv = Arrays.copyOfRange(combined, 0, GCM_IV_LENGTH);
            byte[] ciphertext = Arrays.copyOfRange(combined, GCM_IV_LENGTH, combined.length);

            SecretKey key = deriveKey(idType);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

            byte[] plainBytes = cipher.doFinal(ciphertext);
            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (CryptoException e) {
            throw e;
        } catch (Exception e) {
            throw new CryptoException("Decryption failed for IdType " + idType.name(), e);
        }
    }

    @Override
    public String encryptObject(Object id, IdType idType) {
        if (id == null) {
            throw new CryptoException("ID object must not be null");
        }
        return encrypt(id.toString(), idType);
    }

    @Override
    public String decryptToString(String encryptedId, IdType idType) {
        return decrypt(encryptedId, idType);
    }

    /**
     * Derives a 256-bit AES key from the master key using SHA-256 with the IdType prefix as salt.
     */
    private SecretKey deriveKey(IdType idType) {
        return derivedKeys.computeIfAbsent(idType, type -> {
            try {
                String raw = masterKey + ":" + type.getPrefix();
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] keyBytes = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
                return new SecretKeySpec(keyBytes, KEY_ALGORITHM);
            } catch (Exception e) {
                throw new CryptoException("Key derivation failed for IdType " + type.name(), e);
            }
        });
    }
}
