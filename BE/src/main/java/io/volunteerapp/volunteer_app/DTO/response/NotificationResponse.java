package io.volunteerapp.volunteer_app.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Integer id;
    private String title;
    private String content;
    private String attached;
    private Integer senderId;
    private String senderName;
    private String senderRole; // ADMIN | ORGANIZATION | SYSTEM
    private String type; // PERSONAL | ORGANIZATION | GLOBAL
    private Instant createdAt;
}
