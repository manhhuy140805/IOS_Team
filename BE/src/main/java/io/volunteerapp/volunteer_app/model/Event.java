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

    private String description;

    // vị trí đưa ra
    private String location;

    // thời gian mở đăng ky khác với thời gian diễn ra sự kiện
    // đây là thời gian diễn ra sự kiện
    @Column(nullable = false)
    private Date eventStartTime;

    @Column(nullable = false)
    private Date eventEndTime;

    // đây là thời gian cho phép đăng ky sự kiện
    private Date registrationOpenTime;
    private Date registrationCloseTime;

    // số lượng người đăng kí tối đa
    @Column(nullable = false)
    private Integer numOfVolunteers;

    // số sao thưởng có thể null vì có thể sự kiện không có thưởng
    private Integer rewardPoints;

    private String status;

    @Column(nullable = true)
    private Boolean hasCertificate = false;

    private Instant createdAt;

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
    public void hanleBeforeCreate() {
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedAt = Instant.now();
    }

}
