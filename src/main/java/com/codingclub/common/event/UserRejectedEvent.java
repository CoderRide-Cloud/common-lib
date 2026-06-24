package com.codingclub.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRejectedEvent {
    private Long userId;
    private String username;
    private String email;
    private String reason;
}
