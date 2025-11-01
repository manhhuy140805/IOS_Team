package io.volunteerapp.volunteer_app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class User {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, columnDefinition = "longtext")
    private String fullName;

    @Column(nullable = false, columnDefinition = "longtext")
    private String email;

    @Column(nullable = false, columnDefinition = "longtext")
    private String passwordHash;

    @Column(columnDefinition = "longtext")
    private String phone;

    @Column(columnDefinition = "longtext")
    private String avatarUrl;

    @Column(nullable = false, columnDefinition = "longtext", name = "\"role\"")
    private String role;

    @Column(nullable = false, columnDefinition = "longtext")
    private String status;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "creator")
    private Set<Event> creatorEvents = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<EventRegistration> userEventRegistrations = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<UserReward> userUserRewards = new HashSet<>();

}
