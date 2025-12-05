package io.volunteerapp.volunteer_app.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String fullName;

    private String phone;

    private String avatarUrl;

    @Column(nullable = false)
    private String role = "VOLUNTEER";

    @Column(nullable = false)
    private String status = "ACTIVE";

    @Column(nullable = false)
    private Integer totalPoints = 0;

    private String address;

    private LocalDate dateOfBirth;

    private String gender;

    @Column(nullable = false)
    private Boolean violation = false;

    @Column(nullable = false)
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
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedAt = Instant.now();
    }
}
