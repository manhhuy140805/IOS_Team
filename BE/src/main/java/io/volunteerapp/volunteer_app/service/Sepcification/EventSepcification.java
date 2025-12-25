package io.volunteerapp.volunteer_app.service.Sepcification;

import io.volunteerapp.volunteer_app.model.Event;
import io.volunteerapp.volunteer_app.model.EventType;
import io.volunteerapp.volunteer_app.model.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Date;

public class EventSepcification {

    public static Specification<Event> hasTitle(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null || title.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

    public static Specification<Event> hasLocation(String location) {
        return (root, query, criteriaBuilder) -> {
            if (location == null || location.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("location")),
                    "%" + location.toLowerCase() + "%");
        };
    }

    public static Specification<Event> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null || status.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<Event> hasEventType(Integer eventTypeId) {
        return (root, query, criteriaBuilder) -> {
            if (eventTypeId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Event, EventType> eventTypeJoin = root.join("eventType");
            return criteriaBuilder.equal(eventTypeJoin.get("id"), eventTypeId);
        };
    }

    public static Specification<Event> hasCreator(Integer creatorId) {
        return (root, query, criteriaBuilder) -> {
            if (creatorId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Event, User> creatorJoin = root.join("creator");
            return criteriaBuilder.equal(creatorJoin.get("id"), creatorId);
        };
    }

    public static Specification<Event> eventStartTimeAfter(Date startDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("eventStartTime"), startDate);
        };
    }

    public static Specification<Event> eventStartTimeBefore(Date endDate) {
        return (root, query, criteriaBuilder) -> {
            if (endDate == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("eventStartTime"), endDate);
        };
    }

    public static Specification<Event> hasRewardPoints(Boolean hasReward) {
        return (root, query, criteriaBuilder) -> {
            if (hasReward == null) {
                return criteriaBuilder.conjunction();
            }
            if (hasReward) {
                return criteriaBuilder.isNotNull(root.get("rewardPoints"));
            } else {
                return criteriaBuilder.isNull(root.get("rewardPoints"));
            }
        };
    }

    // Filter events where registration is still open (eventEndTime >= today)
    public static Specification<Event> registrationOpen() {
        return (root, query, criteriaBuilder) -> {
            Date today = new Date(System.currentTimeMillis());
            return criteriaBuilder.greaterThanOrEqualTo(root.get("eventEndTime"), today);
        };
    }

    // Filter events where registration is still open (eventEndTime >= provided
    // date)
    public static Specification<Event> registrationOpenAfter(Date date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("eventEndTime"), date);
        };
    }
}
