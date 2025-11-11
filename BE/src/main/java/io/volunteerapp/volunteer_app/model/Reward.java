package io.volunteerapp.volunteer_app.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Reward {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(name = "\"description\"")
    private String description;

    @Column(nullable = false)
    private Integer pointsRequired;

    @Column(nullable = false)
    private Integer quantity;

    private Instant createdAt;

    private Instant updatedAt;

    @OneToMany(mappedBy = "reward")
    private Set<UserReward> rewardUserRewards = new HashSet<>();

    @PrePersist
    public void handleBeforeCreate() {
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedAt = Instant.now();
    }
}
