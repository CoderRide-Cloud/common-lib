package com.codingclub.common.web;

import com.codingclub.common.security.AuthUserContext;
import org.springframework.stereotype.Component;

@Component
public class AuthContextResolver {

    public AuthUserContext resolve(
            String userId,
            String role,
            String permissions,
            String position,
            String isLead,
            String isActive,
            String customRoleId) {
        return AuthUserContext.fromHeaders(userId, role, permissions, position, isLead, isActive, customRoleId);
    }
}
