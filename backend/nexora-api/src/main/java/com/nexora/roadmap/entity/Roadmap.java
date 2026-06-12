package com.nexora.roadmap.entity;

import com.nexora.common.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a structured learning Roadmap in the Nexora system.
 * A Roadmap consists of sequential milestones that guide candidates through learning topics.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "roadmaps")
public class Roadmap extends BaseEntity {

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", nullable = false, length = 50)
    private DifficultyLevel difficultyLevel;

    @Column(name = "estimated_weeks", nullable = false)
    private Integer estimatedWeeks;

    @OneToMany(mappedBy = "roadmap", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Milestone> milestones = new ArrayList<>();

    /**
     * Helper method to associate a Milestone with this Roadmap, maintaining bidirectional referential integrity.
     *
     * @param milestone The Milestone entity to add.
     */
    public void addMilestone(Milestone milestone) {
        if (milestone != null) {
            milestones.add(milestone);
            milestone.setRoadmap(this);
        }
    }

    /**
     * Helper method to remove a Milestone association, maintaining bidirectional referential integrity.
     *
     * @param milestone The Milestone entity to remove.
     */
    public void removeMilestone(Milestone milestone) {
        if (milestone != null) {
            milestones.remove(milestone);
            milestone.setRoadmap(null);
        }
    }
}
