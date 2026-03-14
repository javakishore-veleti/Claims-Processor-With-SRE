package com.healthcare.claims.common.crypto;

/**
 * Runtime exception thrown when an encryption or decryption operation fails.
 */
public class CryptoException extends RuntimeException {

    public CryptoException(String message) {
        super(message);
    }

    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }
}
