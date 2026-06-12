package com.nexora.roadmap.entity;

import com.nexora.common.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
 * Entity representing a Milestone within a Roadmap.
 * Milestones sequence learning steps and aggregate specific learning Tasks.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "milestones",
    indexes = @Index(name = "idx_milestones_roadmap_id", columnList = "roadmap_id")
)
public class Milestone extends BaseEntity {

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "sequence_order", nullable = false)
    private Integer sequenceOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_id", nullable = false)
    private Roadmap roadmap;

    @OneToMany(mappedBy = "milestone", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();

    /**
     * Helper method to associate a Task with this Milestone, maintaining bidirectional referential integrity.
     *
     * @param task The Task entity to add.
     */
    public void addTask(Task task) {
        if (task != null) {
            tasks.add(task);
            task.setMilestone(this);
        }
    }

    /**
     * Helper method to remove a Task association, maintaining bidirectional referential integrity.
     *
     * @param task The Task entity to remove.
     */
    public void removeTask(Task task) {
        if (task != null) {
            tasks.remove(task);
            task.setMilestone(null);
        }
    }
}
