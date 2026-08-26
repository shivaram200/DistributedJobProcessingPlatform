package com.projectone.distributedjobprocessingplatform.controller;


import com.projectone.distributedjobprocessingplatform.dto.CreateJobRequest;
import com.projectone.distributedjobprocessingplatform.dto.JobResponse;
import com.projectone.distributedjobprocessingplatform.dto.JobStatusResponse;
import com.projectone.distributedjobprocessingplatform.service.JobService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService){
        this.jobService = jobService;
    }


    @PostMapping
    public ResponseEntity<JobResponse> createJob(@RequestBody CreateJobRequest createJobRequest){



        JobResponse jobResponse = jobService.createJob(createJobRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobResponse);



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

}
