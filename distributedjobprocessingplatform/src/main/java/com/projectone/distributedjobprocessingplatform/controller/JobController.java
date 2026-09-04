package com.projectone.distributedjobprocessingplatform.controller;


import com.projectone.distributedjobprocessingplatform.dto.CreateJobRequest;
import com.projectone.distributedjobprocessingplatform.dto.JobResponse;
import com.projectone.distributedjobprocessingplatform.dto.JobStatusResponse;
import com.projectone.distributedjobprocessingplatform.dto.UpdateJobStatusRequest;
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

    public JobController(JobService jobService, JobWorker jobWorker){
        this.jobService = jobService;
        this.jobWorker = jobWorker;
    }


    @PostMapping
    public ResponseEntity<JobResponse> createJob(@Valid  @RequestBody CreateJobRequest createJobRequest){



        JobResponse jobResponse = jobService.createJob(createJobRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobResponse);



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
