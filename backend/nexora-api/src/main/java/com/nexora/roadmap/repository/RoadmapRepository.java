package com.nexora.roadmap.repository;

import com.nexora.roadmap.entity.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data JPA Repository interface for performing database operations on {@link Roadmap}.
 */
@Repository
public interface RoadmapRepository extends JpaRepository<Roadmap, UUID> {
}
