package com.projectone.distributedjobprocessingplatform.Dao;

import com.projectone.distributedjobprocessingplatform.Entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job,Integer> {

}
