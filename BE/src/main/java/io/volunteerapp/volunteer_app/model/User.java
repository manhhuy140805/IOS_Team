package io.volunteerapp.volunteer_app.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users") // Đổi tên table vì "user" là reserved keyword trong PostgreSQL
@Getter
@Setter
public class User {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String fullName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private String phone;

    @Column
    private String avatarUrl;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String status;

    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "creator")
    private Set<Event> creatorEvents = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<EventRegistration> userEventRegistrations = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<UserReward> userUserRewards = new HashSet<>();

    @PrePersist
    public void handleBeforeCreate() {
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedAt = Instant.now();
    }
}
