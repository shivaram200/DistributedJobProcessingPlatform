# Distributed Job Processing Platform

A scalable, fault-tolerant background job processing platform built with **Java, Spring Boot, PostgreSQL, Redis, and Apache Kafka**.

The platform allows applications to submit background jobs such as PDF processing, report generation, email delivery, image processing, payment reconciliation, and notification delivery.

Instead of executing expensive operations directly inside an HTTP request, jobs are submitted to the platform and processed asynchronously by a distributed worker cluster.

---

## 🚀 Why This Project?

Many backend systems need to perform operations that are:

* Time-consuming
* CPU-intensive
* I/O-intensive
* Retryable
* Asynchronous
* Not suitable for blocking an HTTP request

For example:

```text
User → Upload PDF → API → Process PDF → Response
```

If PDF processing takes 30 seconds, the API request becomes slow and unreliable.

This platform changes the architecture to:

```text
Client
   │
   ▼
REST API
   │
   ▼
Job Service
   │
   ▼
Message Broker
   │
   ├──────────────┐
   ▼              ▼
Worker 1       Worker 2
   │              │
   └──────┬───────┘
          ▼
       Database
```

The API only accepts the job and returns a job ID.

Workers process the job asynchronously.

---

# 🎯 Core Features

## 1. Job Creation

Clients can submit jobs through a REST API.

Example:

```http
POST /api/v1/jobs
```

Request:

```json
{
  "type": "PDF_PROCESSING",
  "payload": {
    "fileId": "file-123"
  },
  "priority": 8,
  "idempotencyKey": "pdf-processing-123"
}
```

Response:

```json
{
  "jobId": "job-8f31",
  "status": "QUEUED"
}
```

---

## 2. Job Priority

Jobs can have different priorities.

Example:

```text
Priority 10 → Critical
Priority 5  → Normal
Priority 1  → Low
```

Higher-priority jobs should be processed before lower-priority jobs when possible.

This demonstrates how the platform handles **work scheduling** rather than simply processing requests sequentially.

---

# 🔄 Job Lifecycle

A job moves through several states:

```text
CREATED
   │
   ▼
QUEUED
   │
   ▼
PROCESSING
   │
   ├───────────────┐
   ▼               ▼
COMPLETED        FAILED
                   │
                   ▼
                RETRYING
                   │
                   ▼
                QUEUED
```

After the maximum number of retries:

```text
FAILED
   │
   ▼
DEAD LETTER QUEUE
```

---

# 🔁 Retry Mechanism

Temporary failures should not immediately cause permanent job failure.

For example:

```text
Attempt 1 → Failed
     ↓
Wait 1 second

Attempt 2 → Failed
     ↓
Wait 2 seconds

Attempt 3 → Failed
     ↓
Wait 4 seconds

Attempt 4 → Failed
     ↓
Dead Letter Queue
```

The retry delay follows exponential backoff:

```text
delay = initialDelay × 2^retryCount
```

This prevents the system from continuously hammering a failing dependency.

---

# 🆔 Idempotency

Distributed systems can sometimes process the same job more than once.

For example:

```text
Worker A → processes Job 123
Worker A crashes
        ↓
Message becomes available again
        ↓
Worker B → processes Job 123
```

Without protection, the operation could happen twice.

The platform therefore supports **idempotency keys** and job execution safeguards.

Example:

```text
idempotencyKey = payment-reconciliation-123
```

If the same request is submitted again, the system can recognize that it has already been accepted or processed.

---

# 💀 Dead Letter Queue

Jobs that repeatedly fail are moved to a Dead Letter Queue.

Example:

```text
Job
 ↓
Attempt 1 ❌
 ↓
Attempt 2 ❌
 ↓
Attempt 3 ❌
 ↓
Attempt 4 ❌
 ↓
DLQ
```

This prevents permanently failing jobs from blocking the normal processing pipeline.

Operators can later inspect and potentially replay these jobs.

---

# 👷 Worker Registration

Workers register themselves with the platform.

Example:

```text
Worker-1
Worker-2
Worker-3
Worker-4
```

Each worker advertises information such as:

```text
workerId
supportedJobTypes
status
lastHeartbeat
capacity
```

The platform can therefore determine which workers are alive and capable of processing particular jobs.

---

# ❤️ Worker Heartbeats

Workers periodically send heartbeats.

```text
Worker
   │
   │ heartbeat
   ▼
Job Service
```

If a worker stops sending heartbeats:

```text
Worker
   │
   X
   │
   ▼
Heartbeat timeout
   │
   ▼
Worker marked DEAD
```

This helps the system detect failed workers.

---

# 🚦 Rate Limiting

The API supports rate limiting to prevent clients from overwhelming the system.

For example:

```text
Client A
   ↓
100 requests/minute
```

Requests exceeding the configured limit can receive:

```http
HTTP 429 Too Many Requests
```

Redis can be used to maintain distributed rate-limit state.

---

# 🔐 Authentication

The platform protects APIs using authentication and authorization.

Example:

```text
Client
   │
   │ JWT
   ▼
Authentication
   │
   ▼
Authorization
   │
   ▼
Job API
```

Different clients can have different permissions.

---

# 📊 Metrics & Observability

The platform exposes metrics such as:

### Job Metrics

```text
jobs_created_total
jobs_completed_total
jobs_failed_total
jobs_retried_total
jobs_dead_lettered_total
```

### Worker Metrics

```text
active_workers
dead_workers
worker_processing_count
```

### Performance Metrics

```text
job_processing_latency
queue_wait_time
job_success_rate
```

These metrics allow operators to understand how the system behaves under load.

---

# 🏗️ Architecture

```text
                         ┌──────────────────┐
                         │      Client      │
                         └────────┬─────────┘
                                  │
                                  ▼
                         ┌──────────────────┐
                         │    API Gateway   │
                         └────────┬─────────┘
                                  │
                                  ▼
                         ┌──────────────────┐
                         │   Job Service    │
                         │                  │
                         │ Authentication   │
                         │ Validation       │
                         │ Idempotency      │
                         │ Rate Limiting    │
                         └────────┬─────────┘
                                  │
                    ┌─────────────┴─────────────┐
                    │                           │
                    ▼                           ▼
             ┌─────────────┐             ┌─────────────┐
             │ PostgreSQL  │             │    Redis    │
             │             │             │             │
             │ Jobs        │             │ Rate limits │
             │ Workers     │             │ Caching     │
             │ Attempts    │             │ Locks       │
             └─────────────┘             └─────────────┘

                                  │
                                  ▼
                         ┌──────────────────┐
                         │   Kafka Broker   │
                         │                  │
                         │ Job Queue        │
                         │ Retry Queue      │
                         │ Dead Letter      │
                         └────────┬─────────┘
                                  │
                    ┌─────────────┼─────────────┐
                    │             │             │
                    ▼             ▼             ▼
               ┌────────┐    ┌────────┐    ┌────────┐
               │Worker 1│    │Worker 2│    │Worker 3│
               └────┬───┘    └────┬───┘    └────┬───┘
                    │              │              │
                    └──────────────┼──────────────┘
                                   ▼
                            Job Execution
```

---

# 🧩 Main Components

## Job Service

Responsible for:

* Creating jobs
* Validating requests
* Maintaining job state
* Enforcing idempotency
* Publishing jobs to Kafka
* Tracking job attempts
* Exposing job status APIs

---

## Message Broker

Kafka acts as the asynchronous communication layer between the Job Service and Worker Cluster.

Example:

```text
Job Service
     │
     ▼
Kafka
     │
     ├── job-processing
     ├── retry
     └── dead-letter
```

This decouples job submission from job execution.

---

## Worker Service

Workers consume jobs from Kafka and execute them.

Each worker:

```text
1. Receives job
2. Validates job
3. Updates status → PROCESSING
4. Executes operation
5. Records result
6. Marks job COMPLETED
```

If execution fails:

```text
Worker
  │
  ▼
Failure
  │
  ▼
Retry decision
  │
  ├── Retry available → Retry Queue
  │
  └── Retry exhausted → DLQ
```

---

# 🗄️ Data Model

### Job

```text
Job
-------------------------
id
type
payload
priority
status
idempotency_key
retry_count
max_retries
created_at
updated_at
scheduled_at
completed_at
```

### Job Attempt

```text
JobAttempt
-------------------------
id
job_id
worker_id
attempt_number
status
error_message
started_at
completed_at
```

### Worker

```text
Worker
-------------------------
id
hostname
status
supported_job_types
last_heartbeat
capacity
registered_at
```

---

# 🌐 REST API

### Create Job

```http
POST /api/v1/jobs
```

### Get Job

```http
GET /api/v1/jobs/{jobId}
```

### Cancel Job

```http
POST /api/v1/jobs/{jobId}/cancel
```

### Retry Job

```http
POST /api/v1/jobs/{jobId}/retry
```

### List Jobs

```http
GET /api/v1/jobs
```

### Worker Registration

```http
POST /api/v1/workers/register
```

### Worker Heartbeat

```http
POST /api/v1/workers/{workerId}/heartbeat
```

### Worker Status

```http
GET /api/v1/workers
```

---

# 🛠️ Technology Stack

| Technology      | Purpose                                             |
| --------------- | --------------------------------------------------- |
| Java            | Primary programming language                        |
| Spring Boot     | Backend framework                                   |
| Spring Security | Authentication & authorization                      |
| Spring Data JPA | Database access                                     |
| PostgreSQL      | Persistent job state                                |
| Apache Kafka    | Distributed message broker                          |
| Redis           | Rate limiting, caching and distributed coordination |
| Docker          | Containerization                                    |
| Docker Compose  | Local infrastructure                                |
| Maven           | Build & dependency management                       |
| JUnit           | Unit testing                                        |
| Mockito         | Mocking                                             |
| Testcontainers  | Integration testing                                 |
| Prometheus      | Metrics                                             |
| Grafana         | Monitoring                                          |
| GitHub Actions  | CI/CD                                               |

---

# 📦 Project Structure

```text
distributed-job-platform/
│
├── job-service/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   ├── dto/
│   ├── kafka/
│   ├── security/
│   └── config/
│
├── worker-service/
│   ├── consumer/
│   ├── executor/
│   ├── heartbeat/
│   ├── retry/
│   └── config/
│
├── common/
│   ├── model/
│   ├── events/
│   └── exceptions/
│
├── docker/
│   └── docker-compose.yml
│
├── docs/
│   ├── architecture/
│   ├── api/
│   └── decisions/
│
└── README.md
```

---

# 🔥 Engineering Challenges

This project intentionally focuses on real distributed-system problems.

### 1. What happens when a worker crashes?

The system must detect the failure and ensure the job can eventually be processed again.

### 2. What happens when a message is delivered twice?

Idempotency prevents duplicate side effects.

### 3. What happens when a dependency is temporarily unavailable?

Exponential backoff prevents aggressive retries.

### 4. What happens when a job permanently fails?

The job is moved to the Dead Letter Queue.

### 5. What happens when thousands of jobs arrive simultaneously?

Kafka provides buffering while workers process jobs asynchronously.

### 6. What happens when more processing capacity is required?

Additional workers can be started:

```text
Worker 1
Worker 2
Worker 3
        ↓
Need more capacity
        ↓
Worker 4
Worker 5
Worker 6
```

This demonstrates **horizontal scaling**.

---

# 📈 Scalability

The worker layer is designed to scale horizontally.

Instead of increasing the CPU of one worker:

```text
Worker
8 CPU
32 GB RAM
```

we can add more workers:

```text
Worker 1
Worker 2
Worker 3
Worker 4
Worker 5
```

Kafka distributes work across consumers.

This allows the processing capacity of the system to grow as workload increases.

---

# 🧪 Testing Strategy

The project includes multiple levels of testing.

### Unit Tests

Test individual components:

```text
RetryService
JobService
IdempotencyService
RateLimitService
```

### Integration Tests

Test:

```text
Spring Boot
    +
PostgreSQL
    +
Kafka
    +
Redis
```

using Testcontainers.

### Failure Testing

The system will intentionally simulate:

```text
Worker crash
Kafka unavailable
Database failure
Processing timeout
Repeated job failure
Duplicate job submission
```

The objective is to verify that the platform behaves correctly under failure.

---

# 🐳 Running Locally

Start infrastructure:

```bash
docker compose up -d
```

Run the Job Service:

```bash
mvn spring-boot:run
```

Run multiple workers:

```bash
mvn spring-boot:run
```

Start additional worker instances to simulate horizontal scaling.

---

# 📊 Example Workflow

A client submits:

```json
{
  "type": "REPORT_GENERATION",
  "priority": 8,
  "payload": {
    "reportId": "report-123"
  }
}
```

The platform responds:

```json
{
  "jobId": "job-123",
  "status": "QUEUED"
}
```

The job enters Kafka:

```text
Kafka
  │
  ▼
Worker 2
```

Worker 2 starts execution:

```text
QUEUED
   ↓
PROCESSING
```

Suppose the report service temporarily fails:

```text
PROCESSING
     ↓
FAILED
     ↓
RETRYING
```

After exponential backoff:

```text
RETRYING
     ↓
QUEUED
     ↓
Worker 3
     ↓
COMPLETED
```

The client can check:

```http
GET /api/v1/jobs/job-123
```

and receive:

```json
{
  "jobId": "job-123",
  "status": "COMPLETED",
  "attempts": 2
}
```

---

# 🎓 What This Project Demonstrates

This project demonstrates practical knowledge of:

* Java
* Spring Boot
* REST API design
* Spring Security
* PostgreSQL
* JPA/Hibernate
* Kafka
* Redis
* Distributed systems
* Asynchronous processing
* Horizontal scaling
* Fault tolerance
* Retry strategies
* Exponential backoff
* Idempotency
* Rate limiting
* Dead Letter Queues
* Worker coordination
* Observability
* Metrics
* Docker
* Integration testing
* CI/CD

More importantly, it demonstrates the ability to reason about **what happens when distributed systems fail**.

---

# 🚧 Future Improvements

Potential extensions include:

* Scheduled jobs
* Cron-based jobs
* Job dependencies
* Job cancellation
* Multi-tenant architecture
* Per-tenant quotas
* Priority queues
* Worker autoscaling
* Kubernetes deployment
* Distributed tracing
* OpenTelemetry
* WebSocket job notifications
* Admin dashboard
* Job replay from DLQ
* Circuit breakers
* Exactly-once business processing guarantees

---

# 👨‍💻 Author

Built as a production-style backend engineering project to explore scalable Java backend systems and distributed architecture.
