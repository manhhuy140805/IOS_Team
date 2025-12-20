package io.volunteerapp.volunteer_app.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "notifications")
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String attached;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    private String senderRole; // ADMIN | ORGANIZATION | SYSTEM

    private String type = "PERSONAL"; // PERSONAL | ORGANIZATION | GLOBAL

    private Instant createdAt;

    @OneToMany(mappedBy = "notification")
    private Set<UserNotification> userNotifications = new HashSet<>();

    @PrePersist
    public void handleBeforeCreate() {
        this.createdAt = Instant.now();
    }
}
