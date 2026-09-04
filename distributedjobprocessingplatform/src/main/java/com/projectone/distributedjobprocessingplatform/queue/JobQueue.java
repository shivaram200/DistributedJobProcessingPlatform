package com.projectone.distributedjobprocessingplatform.queue;

import com.projectone.distributedjobprocessingplatform.dto.JobMessage;

public interface JobQueue {

    void publish(JobMessage message);

    JobMessage consume();

}
