package io.volunteerapp.volunteer_app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

    @Column(nullable = false, columnDefinition = "longtext")
    private String title;

    @Column(
            nullable = false,
            columnDefinition = "longtext",
            name = "\"description\""
    )
    private String description;

    @Column(nullable = false, columnDefinition = "longtext")
    private String address;

    @Column(nullable = false, columnDefinition = "longtext")
    private String location;

    @Column(nullable = false)
    private OffsetDateTime startTime;

    @Column(nullable = false)
    private OffsetDateTime endTime;

    @Column
    private Integer capacity;

    @Column(nullable = false)
    private Integer rewardPoints;

    @Column(nullable = false, columnDefinition = "longtext")
    private String status;

    @Column(nullable = false)
    private Integer hasCertificate;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_type_id", nullable = false)
    private EventType eventType;

    @OneToMany(mappedBy = "event")
    private Set<EventRegistration> eventEventRegistrations = new HashSet<>();

}
