package com.projectone.distributedjobprocessingplatform.service;


import com.projectone.distributedjobprocessingplatform.dto.CreateJobRequest;
import com.projectone.distributedjobprocessingplatform.dto.JobMessage;
import com.projectone.distributedjobprocessingplatform.dto.JobResponse;
import com.projectone.distributedjobprocessingplatform.dto.JobStatusResponse;
import com.projectone.distributedjobprocessingplatform.entity.Job;
import com.projectone.distributedjobprocessingplatform.entity.JobAttempt;
import com.projectone.distributedjobprocessingplatform.entity.JobStatus;
import com.projectone.distributedjobprocessingplatform.exception.InvalidJobException;
import com.projectone.distributedjobprocessingplatform.exception.InvalidJobStatusTransitionException;
import com.projectone.distributedjobprocessingplatform.exception.JobNotFoundException;
import com.projectone.distributedjobprocessingplatform.queue.JobQueue;
import com.projectone.distributedjobprocessingplatform.repository.JobAttemptRepository;
import com.projectone.distributedjobprocessingplatform.repository.JobRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class JobServiceImpl implements JobService{
    private final JobRepository jobRepository;
    private final JobAttemptRepository jobAttemptRepository;
    private final JobQueue queue;

    public JobServiceImpl(JobRepository jobRepository, JobAttemptRepository jobAttemptRepository, JobQueue queue){
        this.jobRepository=jobRepository;
        this.jobAttemptRepository=jobAttemptRepository;
        this.queue = queue;
    }

    @Override
    public JobResponse createJob(CreateJobRequest createJobRequest) {

        Job job =new Job();

        if(createJobRequest.getType().equals("report_delete")){
            throw new InvalidJobException("Job type should not be report_delete");
        }else{
            job.setType(createJobRequest.getType());
        }



        job.setPayload(createJobRequest.getPayload());
        job.setStatus(JobStatus.PENDING);
        job.setPriority(createJobRequest.getPriority());

        job.setRetryCount(0);

        LocalDateTime now = LocalDateTime.now();

        job.setCreatedAt(now);
        job.setUpdatedAt(now);

        Job savedJob = jobRepository.save(job);

        JobMessage jobMessage = new JobMessage(savedJob.getId(),savedJob.getType(),1);

        queue.publish(jobMessage);

        return convertToJobResponse(savedJob);

    }

    @Override
    @Cacheable(value="jobs",key="#id")
    public JobResponse getJobById(UUID id) {

       Job job = jobRepository.findById(id)
               .orElseThrow(() -> new JobNotFoundException("Job not found : "+id));



        return convertToJobResponse(job);
    }

    @Override
    public JobStatusResponse getJobStatus(UUID id) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found : "+id));

        return convertToJobStatusResponse(job);
    }

    @Override
    @CacheEvict(value = "jobs", key = "#jobId")
    @Transactional
    public void updateJobStatus(UUID jobId, JobStatus newStatus) {
         Job job = jobRepository.findById(jobId)
                 .orElseThrow(() -> new JobNotFoundException("Job not found : "+jobId));

         validateStatusTransition(job.getStatus(),newStatus);

         if(job.getStatus()==JobStatus.PENDING && newStatus==JobStatus.PROCESSING){
             JobAttempt jobAttempt = new JobAttempt();

             jobAttempt.setStatus(JobStatus.PROCESSING);
             jobAttempt.setAttemptNumber(1);
             jobAttempt.setJob(job);
             jobAttempt.setStartedAt(LocalDateTime.now());
             jobAttemptRepository.save(jobAttempt);

         }
         if(job.getStatus()==JobStatus.PROCESSING && (newStatus==JobStatus.COMPLETED || newStatus==JobStatus.FAILED)){
             JobAttempt jobAttempt =jobAttemptRepository.findTopByJobOrderByAttemptNumberDesc(job)
                     .orElseThrow(() -> new IllegalStateException("No Job attempt created for job: "+jobId));
             jobAttempt.setStatus(newStatus);
             jobAttempt.setCompletedAt(LocalDateTime.now());
             jobAttemptRepository.save(jobAttempt);
         }

         job.setStatus(newStatus);
         job.setUpdatedAt(LocalDateTime.now());
         jobRepository.save(job);
    }

    private void validateStatusTransition(
            JobStatus currentStatus,
            JobStatus newStatus) {

        boolean valid = switch (currentStatus) {

            case PENDING ->
                    newStatus == JobStatus.PROCESSING;

            case PROCESSING ->
                    newStatus == JobStatus.COMPLETED
                            || newStatus == JobStatus.FAILED;

            case COMPLETED, FAILED ->
                    false;
        };

        if (!valid) {
            throw new InvalidJobStatusTransitionException(
                    "Cannot change job status from "
                            + currentStatus
                            + " to "
                            + newStatus
            );
        }
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
