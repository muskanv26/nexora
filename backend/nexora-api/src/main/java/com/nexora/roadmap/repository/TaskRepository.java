package com.nexora.roadmap.repository;

import com.nexora.roadmap.entity.Task;
import com.nexora.roadmap.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA Repository interface for performing database operations on
 * {@link Task}.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByMilestoneId(UUID milestoneId);

    long countByMilestoneId(UUID milestoneId);

    long countByMilestoneIdAndStatus(UUID milestoneId, TaskStatus status);

}