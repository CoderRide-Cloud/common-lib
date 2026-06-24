package com.codingclub.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSubmittedEvent {
    private Long projectId;
    private String title;
    private String description;
    private String category;
    private String submitterName;
    private String submitterEmail;
}
