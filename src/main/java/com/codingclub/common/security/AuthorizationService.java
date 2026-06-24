package com.codingclub.common.security;

import com.codingclub.common.exception.ForbiddenException;
import com.codingclub.common.exception.UnauthorizedException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;

@Service
public class AuthorizationService {

    private final RoleHierarchyService roleHierarchyService;

    public AuthorizationService(RoleHierarchyService roleHierarchyService) {
        this.roleHierarchyService = roleHierarchyService;
    }

    public AuthUserContext requireAuthenticated(AuthUserContext user) {
        if (user == null || user.getUserId() == null) {
            throw new UnauthorizedException("Authentication required");
        }
        return user;
    }

    public void requireActive(AuthUserContext user) {
        requireAuthenticated(user);
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new ForbiddenException("Account is not active. Awaiting admin approval.");
        }
    }

    public void requireAdminOrDashboard(AuthUserContext user) {
        requireAuthenticated(user);
        if (user.isAdmin()) {
            return;
        }
        if (hasPermission(user, Permission.VIEW_DASHBOARD)) {
            return;
        }
        throw new ForbiddenException("Admin access required");
    }

    public void requirePermission(AuthUserContext user, Permission... permissions) {
        requireAuthenticated(user);
        if (user.isAdmin()) {
            return;
        }
        Set<Permission> required = Set.of(permissions);
        boolean allowed = user.getPermissions().stream().anyMatch(required::contains);
        if (!allowed) {
            throw new ForbiddenException("Insufficient permissions. Required: " + Arrays.toString(permissions));
        }
    }

    public void requireCanModifyTargetUser(
            AuthUserContext actingUser,
            String targetRole,
            Integer targetCustomRolePosition) {
        requireAuthenticated(actingUser);
        if (!roleHierarchyService.canModifyUser(actingUser, targetRole, targetCustomRolePosition)) {
            throw new ForbiddenException("You cannot modify this user due to role hierarchy restrictions");
        }
    }

    public void requireCanAssignRole(AuthUserContext actingUser, String newRole) {
        requireAuthenticated(actingUser);
        if (!roleHierarchyService.canAssignAdminRole(actingUser, newRole)) {
            throw new ForbiddenException("Only admins can assign the ADMIN role");
        }
    }

    public void requireCanManageRole(AuthUserContext actingUser, int targetRolePosition) {
        requireAuthenticated(actingUser);
        if (!roleHierarchyService.canManageRole(actingUser, targetRolePosition)) {
            throw new ForbiddenException("You cannot manage this role due to hierarchy restrictions");
        }
    }

    public boolean canManageEvents(AuthUserContext user) {
        return user.isAdmin() || Boolean.TRUE.equals(user.getIsLead())
                || hasPermission(user, Permission.MANAGE_EVENTS);
    }

    private boolean hasPermission(AuthUserContext user, Permission permission) {
        return user.getPermissions() != null && user.getPermissions().contains(permission);
    }
}
