package com.projectone.distributedjobprocessingplatform.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;

public class CreateJobRequest {

    @NotBlank
    @Size(min=3,max=100)
    private String type;

    @NotBlank
    private String payload;


    @Min(1)
    @Range(min=1,max = 10)
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
