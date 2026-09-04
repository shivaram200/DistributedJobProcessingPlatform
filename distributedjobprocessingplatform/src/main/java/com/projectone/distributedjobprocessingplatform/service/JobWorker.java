package com.projectone.distributedjobprocessingplatform.service;


import com.projectone.distributedjobprocessingplatform.dto.JobMessage;
import com.projectone.distributedjobprocessingplatform.entity.Job;
import com.projectone.distributedjobprocessingplatform.entity.JobAttempt;
import com.projectone.distributedjobprocessingplatform.entity.JobStatus;
import com.projectone.distributedjobprocessingplatform.exception.JobNotFoundException;
import com.projectone.distributedjobprocessingplatform.queue.JobQueue;
import com.projectone.distributedjobprocessingplatform.repository.JobAttemptRepository;
import com.projectone.distributedjobprocessingplatform.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class JobWorker {
    private final JobProcessor jobProcessor;
    private final JobRepository jobRepository;
    private final JobAttemptRepository jobAttemptRepository;
    private final JobQueue queue;

    public JobWorker(JobProcessor jobProcessor,JobRepository jobRepository,JobAttemptRepository jobAttemptRepository,JobQueue queue){
        this.jobProcessor=jobProcessor;
        this.jobRepository=jobRepository;
        this.jobAttemptRepository=jobAttemptRepository;
        this.queue=queue;
    }

    public void processNextJob(){

        JobMessage message = queue.consume();
        if(message==null){
            System.out.println("No Job Message found to process....");
            return;
        }

        UUID id = message.getJobId();
        Job job = jobRepository.findById(id)
                .orElseThrow(()-> new JobNotFoundException("Job not found: "+id));

        JobAttempt jobAttempt = new JobAttempt();
        jobAttempt.setAttemptNumber(message.getAttempt());
        jobAttempt.setStatus(JobStatus.PROCESSING);
        jobAttempt.setStartedAt(LocalDateTime.now());
        jobAttempt.setJob(job);

        jobAttemptRepository.save(jobAttempt);

        job.setStatus(JobStatus.PROCESSING);
        job.setUpdatedAt(LocalDateTime.now());

        jobRepository.save(job);

        try{
            jobProcessor.processJob(job);

            job.setStatus(JobStatus.COMPLETED);
            job.setUpdatedAt(LocalDateTime.now());
            jobRepository.save(job);

            jobAttempt.setStatus(JobStatus.COMPLETED);
            jobAttempt.setCompletedAt(LocalDateTime.now());
            jobAttemptRepository.save(jobAttempt);

        }catch (Exception e){

            jobAttempt.setStatus(JobStatus.FAILED);
            jobAttempt.setCompletedAt(LocalDateTime.now());
            jobAttempt.setErrorMessage(e.getMessage());
            jobAttemptRepository.save(jobAttempt);

            job.setStatus(JobStatus.FAILED);
            job.setUpdatedAt(LocalDateTime.now());
            jobRepository.save(job);

        }
    }


}
