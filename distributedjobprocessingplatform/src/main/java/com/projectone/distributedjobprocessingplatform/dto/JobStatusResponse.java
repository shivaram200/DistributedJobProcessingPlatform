package com.projectone.distributedjobprocessingplatform.dto;

import com.projectone.distributedjobprocessingplatform.entity.JobStatus;

import java.util.UUID;

public class JobStatusResponse {

   private UUID id;

   private JobStatus status;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }
}
