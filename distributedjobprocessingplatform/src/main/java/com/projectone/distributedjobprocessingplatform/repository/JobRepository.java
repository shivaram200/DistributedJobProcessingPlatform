package com.projectone.distributedjobprocessingplatform.repository;

import com.projectone.distributedjobprocessingplatform.entity.Job;
import com.projectone.distributedjobprocessingplatform.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;



public interface JobRepository extends JpaRepository<Job, UUID> {

    Optional<Job> findFirstByStatusOrderByPriorityDesc(JobStatus status);

}
