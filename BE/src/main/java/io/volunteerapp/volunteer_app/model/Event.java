package io.volunteerapp.volunteer_app.model;

import jakarta.persistence.*;

import java.sql.Date;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Event {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(length = 5000)
    private String description;

    private String location;

    private String imageUrl;

    @Column(nullable = false)
    private Date eventStartTime;

    @Column(nullable = false)
    private Date eventEndTime;

    @Column(nullable = false)
    private Integer numOfVolunteers;

    private Integer rewardPoints;

    @Column(nullable = false)
    private String status = "PENDING";

    private String category;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_type_id", nullable = false)
    private EventType eventType;

    @OneToMany(mappedBy = "event")
    private Set<EventRegistration> eventEventRegistrations = new HashSet<>();

    @PrePersist
    public void handleBeforeCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedAt = Instant.now();
    }

}
