package com.projectone.distributedjobprocessingplatform.repository;

import com.projectone.distributedjobprocessingplatform.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {

    boolean existsByEventId(UUID eventId);

    Optional<ProcessedEvent> findByJobId(UUID jobId);

    Optional<ProcessedEvent> findByEventId(UUID eventId);

}
