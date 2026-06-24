package com.codingclub.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventCreatedEvent {
    private Long eventId;
    private String title;
    private Long createdBy;
    private LocalDateTime eventDate;
}
