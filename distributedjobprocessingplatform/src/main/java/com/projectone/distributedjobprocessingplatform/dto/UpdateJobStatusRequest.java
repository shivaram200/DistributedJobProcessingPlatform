package com.projectone.distributedjobprocessingplatform.dto;

import com.projectone.distributedjobprocessingplatform.entity.JobStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateJobStatusRequest {

    @NotNull
    private JobStatus status;

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }
}