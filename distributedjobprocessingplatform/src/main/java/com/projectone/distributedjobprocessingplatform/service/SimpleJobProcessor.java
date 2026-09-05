package com.projectone.distributedjobprocessingplatform.service;

import com.projectone.distributedjobprocessingplatform.entity.Job;
import org.springframework.stereotype.Service;


@Service
public class SimpleJobProcessor implements JobProcessor{
    @Override
    public void processJob(Job job) {
        System.out.println(Thread.currentThread().getName()+ " Started Job processing for job "+job.getId()+" has started");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Job processing got interrupted :",e);
        }

        System.out.println(Thread.currentThread().getName()+" Finished executing job "+job.getId());
    }
}
