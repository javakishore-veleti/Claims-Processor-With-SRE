package com.healthcare.claims.common.crypto;

/**
 * Service for encrypting and decrypting identifiers.
 * Different implementations can be activated via the {@code claims.crypto.enabled} property.
 */
public interface EncryptionService {

    /**
     * Encrypts a plain-text identifier for the given {@link IdType}.
     *
     * @param plainId the plain identifier string
     * @param idType  the type of identifier (used for key derivation / prefixing)
     * @return the encrypted (or decorated) identifier
     */
    String encrypt(String plainId, IdType idType);

    /**
     * Decrypts an encrypted identifier back to its plain-text form.
     *
     * @param encryptedId the encrypted identifier
     * @param idType      the type of identifier
     * @return the original plain identifier
     */
    String decrypt(String encryptedId, IdType idType);

    /**
     * Convenience method that converts an arbitrary id object (UUID, Long, etc.)
     * to its string representation and then encrypts it.
     *
     * @param id     the identifier object
     * @param idType the type of identifier
     * @return the encrypted identifier string
     */
    String encryptObject(Object id, IdType idType);

    /**
     * Decrypts an encrypted identifier and returns the plain string.
     * Functionally equivalent to {@link #decrypt(String, IdType)} but provided
     * for symmetry with {@link #encryptObject(Object, IdType)}.
     *
     * @param encryptedId the encrypted identifier
     * @param idType      the type of identifier
     * @return the decrypted plain string
     */
    String decryptToString(String encryptedId, IdType idType);
}
