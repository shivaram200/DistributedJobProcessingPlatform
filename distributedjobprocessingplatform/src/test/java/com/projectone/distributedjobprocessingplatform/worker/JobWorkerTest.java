package com.projectone.distributedjobprocessingplatform.worker;

import com.projectone.distributedjobprocessingplatform.entity.Job;
import com.projectone.distributedjobprocessingplatform.entity.JobStatus;
import com.projectone.distributedjobprocessingplatform.repository.JobRepository;
import com.projectone.distributedjobprocessingplatform.service.JobWorker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class JobWorkerTest {

    @Autowired
    private JobWorker jobWorker;

    @Autowired
    private JobRepository jobRepository;


    @Test
    void twoWorkersCanProcessSameJob() throws InterruptedException {

        jobWorker.processNextJob();
    }





}