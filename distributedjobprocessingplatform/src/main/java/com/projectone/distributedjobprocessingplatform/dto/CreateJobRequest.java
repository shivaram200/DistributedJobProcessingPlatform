package com.projectone.distributedjobprocessingplatform.dto;

public class CreateJobRequest {

    private String type;

    private String payload;

    private Integer priority;

    public String getType() {
        return type;
    }

    public String getPayload() {
        return payload;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

}
