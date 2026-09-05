package com.projectone.distributedjobprocessingplatform.dto;

import java.util.UUID;

public class JobMessage {

    private UUID eventId;

    private UUID jobId;

    private String jobType;

    private int attempt;

    public JobMessage(){

    }

    public JobMessage(UUID eventId,UUID jobId, String jobType, int attempt) {
        this.eventId = eventId;
        this.jobId = jobId;
        this.jobType = jobType;
        this.attempt = attempt;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public UUID getJobId() {
        return jobId;
    }

    public void setJobId(UUID jobId) {
        this.jobId = jobId;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public int getAttempt() {
        return attempt;
    }

    public void setAttempt(int attempt) {
        this.attempt = attempt;
    }
}
