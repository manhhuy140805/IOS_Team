package io.volunteerapp.volunteer_app.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RewardType {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "rewardType")
    private Set<Reward> rewards = new HashSet<>();
}
