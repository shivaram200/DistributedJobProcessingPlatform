package com.projectone.distributedjobprocessingplatform.service;


import com.projectone.distributedjobprocessingplatform.dto.CreateJobRequest;
import com.projectone.distributedjobprocessingplatform.dto.JobResponse;
import com.projectone.distributedjobprocessingplatform.dto.JobStatusResponse;
import com.projectone.distributedjobprocessingplatform.entity.Job;
import com.projectone.distributedjobprocessingplatform.entity.JobStatus;
import com.projectone.distributedjobprocessingplatform.exception.JobNotFoundException;
import com.projectone.distributedjobprocessingplatform.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class JobServiceImpl implements JobService{
    private final JobRepository jobRepository;

    public JobServiceImpl(JobRepository jobRepository){
        this.jobRepository=jobRepository;
    }

    @Override
    public JobResponse createJob(CreateJobRequest createJobRequest) {

        Job job =new Job();

        job.setType(createJobRequest.getType());
        job.setPayload(createJobRequest.getPayload());
        job.setStatus(JobStatus.PENDING);
        job.setPriority(createJobRequest.getPriority());

        job.setRetryCount(0);

        LocalDateTime now = LocalDateTime.now();

        job.setCreatedAt(now);
        job.setUpdatedAt(now);

        Job savedJob = jobRepository.save(job);

        return convertToJobResponse(savedJob);

    }

    @Override
    public JobResponse getJobById(UUID id) {

       Job job = jobRepository.findById(id)
               .orElseThrow(() -> new JobNotFoundException("Job not found :"+id));



        return convertToJobResponse(job);
    }

    @Override
    public JobStatusResponse getJobStatus(UUID id) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found :"+id));

        return convertToJobStatusResponse(job);
    }

    private JobStatusResponse convertToJobStatusResponse(Job job){

        JobStatusResponse jobStatusResponse = new JobStatusResponse();

        jobStatusResponse.setId(job.getId());
        jobStatusResponse.setStatus(job.getStatus());

        return jobStatusResponse;

    }

    private JobResponse convertToJobResponse(Job job){

        JobResponse jobResponse = new JobResponse();

        jobResponse.setId(job.getId());
        jobResponse.setType(job.getType());
        jobResponse.setPayload(job.getPayload());
        jobResponse.setStatus(job.getStatus());
        jobResponse.setPriority(job.getPriority());
        jobResponse.setRetryCount(job.getRetryCount());
        jobResponse.setCreatedAt(job.getCreatedAt());
        jobResponse.setUpdatedAt(job.getUpdatedAt());

        return jobResponse;



    }
}
