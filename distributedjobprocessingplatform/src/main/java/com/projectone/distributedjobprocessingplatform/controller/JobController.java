package com.projectone.distributedjobprocessingplatform.controller;


import com.projectone.distributedjobprocessingplatform.dto.*;
import com.projectone.distributedjobprocessingplatform.entity.Job;
import com.projectone.distributedjobprocessingplatform.entity.ProcessedEvent;
import com.projectone.distributedjobprocessingplatform.exception.JobNotFoundException;
import com.projectone.distributedjobprocessingplatform.queue.JobQueue;
import com.projectone.distributedjobprocessingplatform.repository.JobRepository;
import com.projectone.distributedjobprocessingplatform.repository.ProcessedEventRepository;
import com.projectone.distributedjobprocessingplatform.service.JobService;
import com.projectone.distributedjobprocessingplatform.service.JobWorker;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    private final JobWorker jobWorker;

    private final ProcessedEventRepository processedEventRepository;

    private final JobRepository jobRepository;

    private final JobQueue jobQueue;

    public JobController(JobService jobService, JobWorker jobWorker, ProcessedEventRepository processedEventRepository, JobRepository jobRepository, JobQueue jobQueue){
        this.jobService = jobService;
        this.jobWorker = jobWorker;
        this.processedEventRepository = processedEventRepository;
        this.jobRepository = jobRepository;
        this.jobQueue = jobQueue;
    }


    @PostMapping
    public ResponseEntity<JobResponse> createJob(@Valid  @RequestBody CreateJobRequest createJobRequest){



        JobResponse jobResponse = jobService.createJob(createJobRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobResponse);



    }

    @PostMapping("/{id}/redeliver")
    public ResponseEntity<Void> redeliverJob(@PathVariable UUID id) {

        ProcessedEvent processedEvent = processedEventRepository.findByJobId(id)
                .orElseThrow(() ->
                        new IllegalStateException("No processed event found for job: " + id));

        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new JobNotFoundException("Job not found: " + id));

        JobMessage duplicateMessage = new JobMessage(
                processedEvent.getEventId(),
                job.getId(),
                job.getType(),
                1
        );

        // Simulate broker redelivery of the SAME event
        jobQueue.publish(duplicateMessage);
        jobQueue.publish(duplicateMessage);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/worker/process")
    public ResponseEntity<Void> processJob() {
        jobWorker.processNextJob();
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable UUID id){

        JobResponse response = jobService.getJobById(id);

        return ResponseEntity.ok(response);

    }

    @GetMapping("/{id}/status")
    public ResponseEntity<JobStatusResponse> getJobStatus(@PathVariable UUID id){

        JobStatusResponse response = jobService.getJobStatus(id);

        return ResponseEntity.ok(response);

    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateJobStatus(@PathVariable UUID id, @Valid @RequestBody UpdateJobStatusRequest updateJobStatusRequest){
        jobService.updateJobStatus(id,updateJobStatusRequest.getStatus());

        return ResponseEntity.noContent().build();
    }

}
