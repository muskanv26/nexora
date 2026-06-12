package com.nexora.roadmap.repository;

import com.nexora.roadmap.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data JPA Repository interface for performing database operations on {@link Milestone}.
 */
@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, UUID> {
}
