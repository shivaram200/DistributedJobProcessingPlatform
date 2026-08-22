package com.projectone.distributedjobprocessingplatform.Entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String type;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    private Integer priority;

    private Integer retryCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
