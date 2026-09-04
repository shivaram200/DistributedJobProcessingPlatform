package com.projectone.distributedjobprocessingplatform.queue;

import com.projectone.distributedjobprocessingplatform.dto.JobMessage;
import org.springframework.stereotype.Component;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class InMemoryJobQueue implements JobQueue{

    private final BlockingQueue<JobMessage> queue = new LinkedBlockingQueue<>();


    @Override
    public void publish(JobMessage message) {
         queue.offer(message);
    }

    @Override
    public JobMessage consume() {
        return queue.poll();
    }
}
