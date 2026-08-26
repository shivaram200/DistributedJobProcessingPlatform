package com.projectone.distributedjobprocessingplatform.repository;

import com.projectone.distributedjobprocessingplatform.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;



public interface JobRepository extends JpaRepository<Job, UUID> {

}
