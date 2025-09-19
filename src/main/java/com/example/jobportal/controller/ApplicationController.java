package com.example.jobportal.controller;


import com.example.jobportal.entity.Application;
import com.example.jobportal.entity.ApplicationStatus;
import com.example.jobportal.service.ApplicationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    // Apply for job - moved to top to avoid conflicts
    @PostMapping("/apply")
    public ResponseEntity<?> applyForJob(@RequestBody ApplyJobRequest request) {
        try {
            Application application = applicationService.applyForJob(
                    request.getJobId(),
                    request.getJobSeekerId(),
                    request.getCoverLetter()
            );
            return ResponseEntity.ok("Application submitted successfully! Application ID: " + application.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get applications by jobseeker
    @GetMapping("/job-seeker/{jobSeekerId}")
    public ResponseEntity<List<Application>> getMyApplications(@PathVariable Long jobSeekerId) {
        List<Application> applications = applicationService.getMyApplications(jobSeekerId);
        return ResponseEntity.ok(applications);
    }

    // Get applications for a specific job (employer view)
    @GetMapping("/for-job/{jobId}")
    public ResponseEntity<?> getApplicationsForJob(@PathVariable Long jobId, @RequestParam Long employerId) {
        try {
            List<Application> applications = applicationService.getApplicationsForJob(jobId, employerId);
            return ResponseEntity.ok(applications);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get all applications for an employer
    @GetMapping("/for-employer/{employerId}")
    public ResponseEntity<?> getAllApplicationsForEmployer(@PathVariable Long employerId) {
        try {
            List<Application> applications = applicationService.getAllApplicationsForEmployer(employerId);
            return ResponseEntity.ok(applications);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Update application status
    @PutMapping("/update-status/{applicationId}")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestBody UpdateStatusRequest request) {
        try {
            Application updatedApplication = applicationService.updateApplicationStatus(
                    applicationId,
                    request.getStatus(),
                    request.getEmployerId()
            );
            return ResponseEntity.ok("Application status updated to: " + updatedApplication.getStatus());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get application count for a job
    @GetMapping("/count-for-job/{jobId}")
    public ResponseEntity<Long> getApplicationCount(@PathVariable Long jobId) {
        long count = applicationService.getApplicationCountForJob(jobId);
        return ResponseEntity.ok(count);
    }

    // Get single application by ID - moved to bottom to avoid conflicts
    @GetMapping("/details/{applicationId}")
    public ResponseEntity<Application> getApplicationById(@PathVariable Long applicationId) {
        Optional<Application> application = applicationService.getApplicationById(applicationId);
        if (application.isPresent()) {
            return ResponseEntity.ok(application.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

// Request DTOs
@Data
class ApplyJobRequest {
    private Long jobId;
    private Long jobSeekerId;
    private String coverLetter; // Optional
}

@Data
class UpdateStatusRequest {
    private ApplicationStatus status;
    private Long employerId; // Who is updating the status
}