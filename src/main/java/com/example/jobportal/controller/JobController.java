package com.example.jobportal.controller;


import com.example.jobportal.entity.Job;
import com.example.jobportal.entity.JobType;
import com.example.jobportal.service.JobService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping("/create")
    public ResponseEntity<?> createJob(@RequestBody CreateJobRequest request) {
        try {
            Job job = new Job();
            job.setTitle(request.getTitle());
            job.setDescription(request.getDescription());
            job.setLocation(request.getLocation());
            job.setJobType(request.getJobType());
            job.setSalary(request.getSalary());

            Job savedJob = jobService.createJob(job, request.getEmployerId());
            return ResponseEntity.ok("Job created successfully with ID: " + savedJob.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public List<Job> getAllJobs() {
        return jobService.getAllJobs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable Long id) {
        Optional<Job> job = jobService.getJobById(id);
        if (job.isPresent()) {
            return ResponseEntity.ok(job.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/employer/{employerId}")
    public List<Job> getJobsByEmployer(@PathVariable Long employerId) {
        return jobService.getJobsByEmployer(employerId);
    }

    @GetMapping("/search")
    public List<Job> searchJobs(@RequestParam(required = false) String keyword) {
        return jobService.searchJobs(keyword);
    }

    @GetMapping("/type/{jobType}")
    public List<Job> getJobsByType(@PathVariable JobType jobType) {
        return jobService.getJobsByType(jobType);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateJob(@PathVariable Long id, @RequestBody UpdateJobRequest request) {
        try {
            Job job = new Job();
            job.setTitle(request.getTitle());
            job.setDescription(request.getDescription());
            job.setLocation(request.getLocation());
            job.setJobType(request.getJobType());
            job.setSalary(request.getSalary());

            Job updatedJob = jobService.updateJob(id, job, request.getEmployerId());
            return ResponseEntity.ok("Job updated successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id, @RequestParam Long employerId) {
        try {
            jobService.deleteJob(id, employerId);
            return ResponseEntity.ok("Job deleted successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

// Request DTOs (Data Transfer Objects)
@Data
class CreateJobRequest {
    private String title;
    private String description;
    private String location;
    private JobType jobType;
    private String salary;
    private Long employerId; // Who is creating this job
}

@Data
class UpdateJobRequest {
    private String title;
    private String description;
    private String location;
    private JobType jobType;
    private String salary;
    private Long employerId; // Who is updating this job
}
