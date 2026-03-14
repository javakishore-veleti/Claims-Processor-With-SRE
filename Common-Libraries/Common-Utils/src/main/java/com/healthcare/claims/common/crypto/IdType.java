package com.healthcare.claims.common.crypto;

import lombok.Getter;

@Getter
public enum IdType {

    TENANT("TNT"),
    CUSTOMER("CUS"),
    CLAIM("CLM"),
    USER("USR"),
    GROUP("GRP"),
    ROLE("ROL"),
    PRIVILEGE("PRV"),
    MEMBER("MBR");

    private final String prefix;

    IdType(String prefix) {
        this.prefix = prefix;
    }
}
