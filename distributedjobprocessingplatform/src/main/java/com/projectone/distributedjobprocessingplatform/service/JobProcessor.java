package com.projectone.distributedjobprocessingplatform.service;

import com.projectone.distributedjobprocessingplatform.entity.Job;

public interface JobProcessor {
    void processJob(Job job);
}
