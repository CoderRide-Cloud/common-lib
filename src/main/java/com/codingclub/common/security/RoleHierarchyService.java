package com.codingclub.common.security;

import org.springframework.stereotype.Service;

@Service
public class RoleHierarchyService {

    public int getUserHighestPosition(AuthUserContext user) {
        if (user == null) {
            return -1;
        }
        if (user.isAdmin()) {
            return 999;
        }
        if ("SUSPENDED".equals(user.getRole()) || "PENDING".equals(user.getRole())) {
            return -1;
        }
        return user.getPosition() != null ? user.getPosition() : 0;
    }

    public int getTargetPosition(String targetRole, Integer targetCustomRolePosition) {
        if ("ADMIN".equals(targetRole)) {
            return 999;
        }
        if ("SUSPENDED".equals(targetRole) || "PENDING".equals(targetRole)) {
            return -1;
        }
        return targetCustomRolePosition != null ? targetCustomRolePosition : 0;
    }

    public boolean canModifyUser(AuthUserContext actingUser, String targetRole, Integer targetCustomRolePosition) {
        if (actingUser.isAdmin()) {
            return true;
        }
        if ("ADMIN".equals(targetRole)) {
            return false;
        }
        return getTargetPosition(targetRole, targetCustomRolePosition) < getUserHighestPosition(actingUser);
    }

    public boolean canAssignAdminRole(AuthUserContext actingUser, String newRole) {
        if ("ADMIN".equals(newRole) && !actingUser.isAdmin()) {
            return false;
        }
        return true;
    }

    public boolean canManageRole(AuthUserContext actingUser, int targetRolePosition) {
        if (actingUser.isAdmin()) {
            return true;
        }
        return targetRolePosition < getUserHighestPosition(actingUser);
    }
}
