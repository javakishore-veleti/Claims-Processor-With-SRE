package com.healthcare.claims.api.tenants.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTenantCommand {

    private String name;

    private String displayName;

    private String domain;

    @Pattern(regexp = "^(starter|professional|enterprise)$", message = "Plan must be starter, professional, or enterprise")
    private String plan;

    @Email(message = "Contact email must be valid")
    private String contactEmail;

    private String contactPhone;

    private String address;

    @Min(value = 1, message = "Max users must be at least 1")
    private Integer maxUsers;
}
