package com.healthcare.claims.api.entitlements.command;

import com.healthcare.claims.api.entitlements.model.UserStatus;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserCommand {

    private UUID id;

    private String tenantId;

    @Email(message = "Email must be valid")
    private String email;

    private String firstName;

    private String lastName;

    private String phone;

    private UserStatus status;
}
