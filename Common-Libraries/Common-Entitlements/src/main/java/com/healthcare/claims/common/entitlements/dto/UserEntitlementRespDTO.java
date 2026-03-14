package com.healthcare.claims.common.entitlements.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntitlementRespDTO {

    private String userId;
    private String username;
    private List<RoleRespDTO> roles;
    private List<PrivilegeRespDTO> privileges;
    private List<GroupRespDTO> groups;
}
