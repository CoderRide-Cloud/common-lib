package com.codingclub.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String githubId;
    private String username;
    private String email;
    private String avatarUrl;
    private String githubUrl;
    private String role;
    private Long customRoleId;
    private String suspensionReason;
    private Boolean isActive;
    private Boolean isLead;
}
