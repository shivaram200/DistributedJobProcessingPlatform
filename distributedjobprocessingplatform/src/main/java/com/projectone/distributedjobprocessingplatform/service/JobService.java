package com.projectone.distributedjobprocessingplatform.service;

import com.projectone.distributedjobprocessingplatform.dto.CreateJobRequest;
import com.projectone.distributedjobprocessingplatform.dto.JobResponse;
import com.projectone.distributedjobprocessingplatform.dto.JobStatusResponse;

import java.util.UUID;

public interface JobService {

    JobResponse createJob(CreateJobRequest createJobRequest);
    JobResponse getJobById(UUID id);
    JobStatusResponse getJobStatus(UUID id);
}
