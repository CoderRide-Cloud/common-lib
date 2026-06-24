package com.codingclub.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectApprovedEvent {
    private Long projectId;
    private String title;
    private String submitterName;
    private String submitterEmail;
}
