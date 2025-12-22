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
public class UserNotificationResponse {

    private Integer id;
    private Integer userId;
    private NotificationResponse notification;
    private Boolean isRead;
    private Instant readAt;
    private Instant createdAt;
}
