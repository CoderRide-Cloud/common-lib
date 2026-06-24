package com.codingclub.common.security;

import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Builder
public class AuthUserContext {
    private Long userId;
    private String role;
    private Long customRoleId;
    private Integer position;
    private Boolean isLead;
    private Boolean isActive;
    private Set<Permission> permissions;

    public static AuthUserContext fromHeaders(
            String userIdHeader,
            String roleHeader,
            String permissionsHeader,
            String positionHeader,
            String isLeadHeader,
            String isActiveHeader,
            String customRoleIdHeader) {

        if (userIdHeader == null || roleHeader == null) {
            return null;
        }

        Set<Permission> permissions = permissionsHeader == null || permissionsHeader.isBlank()
                ? Collections.emptySet()
                : Stream.of(permissionsHeader.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Permission::valueOf)
                .collect(Collectors.toSet());

        return AuthUserContext.builder()
                .userId(Long.valueOf(userIdHeader))
                .role(roleHeader)
                .customRoleId(customRoleIdHeader != null && !customRoleIdHeader.isBlank()
                        ? Long.valueOf(customRoleIdHeader) : null)
                .position(positionHeader != null && !positionHeader.isBlank()
                        ? Integer.valueOf(positionHeader) : 0)
                .isLead("true".equalsIgnoreCase(isLeadHeader))
                .isActive(isActiveHeader == null || "true".equalsIgnoreCase(isActiveHeader))
                .permissions(permissions)
                .build();
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
