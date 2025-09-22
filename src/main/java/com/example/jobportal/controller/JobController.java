package com.example.jobportal.controller;

import com.example.jobportal.entity.Job;
import com.example.jobportal.entity.JobType;
import com.example.jobportal.entity.User;
import com.example.jobportal.entity.UserType;
import com.example.jobportal.service.AuthService;
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
    private final AuthService authService;

    @PostMapping("/create")
    public ResponseEntity<?> createJob(@RequestBody CreateJobRequest request) {
        try {
            User currentUser = authService.getCurrentUser();

            if (currentUser.getUserType() != UserType.EMPLOYER) {
                return ResponseEntity.badRequest().body("Only employers can create jobs");
            }

            Job job = new Job();
            job.setTitle(request.getTitle());
            job.setDescription(request.getDescription());
            job.setLocation(request.getLocation());
            job.setJobType(request.getJobType());
            job.setSalary(request.getSalary());

            Job savedJob = jobService.createJob(job, currentUser.getId());
            return ResponseEntity.ok("Job created successfully with ID: " + savedJob.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Public endpoints - no authentication needed
    @GetMapping("/all")
    public List<Job> getAllJobs() {
        return jobService.getAllJobs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable Long id) {
        Optional<Job> job = jobService.getJobById(id);
        return job.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<Job> searchJobs(@RequestParam(required = false) String keyword) {
        return jobService.searchJobs(keyword);
    }

    @GetMapping("/type/{jobType}")
    public List<Job> getJobsByType(@PathVariable JobType jobType) {
        return jobService.getJobsByType(jobType);
    }

    // Protected endpoints - require authentication
    @GetMapping("/my-jobs")
    public ResponseEntity<?> getMyJobs() {
        try {
            User currentUser = authService.getCurrentUser();

            if (currentUser.getUserType() != UserType.EMPLOYER) {
                return ResponseEntity.badRequest().body("Only employers can view their jobs");
            }

            List<Job> jobs = jobService.getJobsByEmployer(currentUser.getId());
            return ResponseEntity.ok(jobs);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateJob(@PathVariable Long id, @RequestBody UpdateJobRequest request) {
        try {
            User currentUser = authService.getCurrentUser();

            if (currentUser.getUserType() != UserType.EMPLOYER) {
                return ResponseEntity.badRequest().body("Only employers can update jobs");
            }

            Job job = new Job();
            job.setTitle(request.getTitle());
            job.setDescription(request.getDescription());
            job.setLocation(request.getLocation());
            job.setJobType(request.getJobType());
            job.setSalary(request.getSalary());

            Job updatedJob = jobService.updateJob(id, job, currentUser.getId());
            return ResponseEntity.ok("Job updated successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id) {
        try {
            User currentUser = authService.getCurrentUser();

            if (currentUser.getUserType() != UserType.EMPLOYER) {
                return ResponseEntity.badRequest().body("Only employers can delete jobs");
            }

            jobService.deleteJob(id, currentUser.getId());
            return ResponseEntity.ok("Job deleted successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

// Clean request DTOs
@Data
class CreateJobRequest {
    private String title;
    private String description;
    private String location;
    private JobType jobType;
    private String salary;
}

@Data
class UpdateJobRequest {
    private String title;
    private String description;
    private String location;
    private JobType jobType;
    private String salary;
}