package com.example.jobportal.controller;

import com.example.jobportal.entity.Application;
import com.example.jobportal.entity.ApplicationStatus;
import com.example.jobportal.entity.User;
import com.example.jobportal.entity.UserType;
import com.example.jobportal.service.ApplicationService;
import com.example.jobportal.service.AuthService;
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
    private final AuthService authService;

    @PostMapping("/apply")
    public ResponseEntity<?> applyForJob(@RequestBody ApplyJobRequest request) {
        try {
            User currentUser = authService.getCurrentUser();

            if (currentUser.getUserType() != UserType.JOB_SEEKER) {
                return ResponseEntity.badRequest().body("Only job seekers can apply for jobs");
            }

            Application application = applicationService.applyForJob(
                    request.getJobId(),
                    currentUser.getId(),
                    request.getCoverLetter()
            );
            return ResponseEntity.ok("Application submitted successfully! Application ID: " + application.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/my-applications")
    public ResponseEntity<?> getMyApplications() {
        try {
            User currentUser = authService.getCurrentUser();

            if (currentUser.getUserType() != UserType.JOB_SEEKER) {
                return ResponseEntity.badRequest().body("Only job seekers can view applications");
            }

            List<Application> applications = applicationService.getMyApplications(currentUser.getId());
            return ResponseEntity.ok(applications);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/for-job/{jobId}")
    public ResponseEntity<?> getApplicationsForJob(@PathVariable Long jobId) {
        try {
            User currentUser = authService.getCurrentUser();

            if (currentUser.getUserType() != UserType.EMPLOYER) {
                return ResponseEntity.badRequest().body("Only employers can view job applications");
            }

            List<Application> applications = applicationService.getApplicationsForJob(jobId, currentUser.getId());
            return ResponseEntity.ok(applications);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/for-my-jobs")
    public ResponseEntity<?> getAllMyJobApplications() {
        try {
            User currentUser = authService.getCurrentUser();

            if (currentUser.getUserType() != UserType.EMPLOYER) {
                return ResponseEntity.badRequest().body("Only employers can view applications");
            }

            List<Application> applications = applicationService.getAllApplicationsForEmployer(currentUser.getId());
            return ResponseEntity.ok(applications);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update-status/{applicationId}")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestBody UpdateStatusRequest request) {
        try {
            User currentUser = authService.getCurrentUser();

            if (currentUser.getUserType() != UserType.EMPLOYER) {
                return ResponseEntity.badRequest().body("Only employers can update application status");
            }

            Application updatedApplication = applicationService.updateApplicationStatus(
                    applicationId,
                    request.getStatus(),
                    currentUser.getId()
            );
            return ResponseEntity.ok("Application status updated to: " + updatedApplication.getStatus());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/details/{applicationId}")
    public ResponseEntity<?> getApplicationById(@PathVariable Long applicationId) {
        try {
            User currentUser = authService.getCurrentUser();
            Optional<Application> application = applicationService.getApplicationById(applicationId);

            if (application.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Application app = application.get();

            // Check permissions: either the job seeker who applied or the employer who owns the job
            boolean isJobSeeker = currentUser.getId().equals(app.getJobSeeker().getId());
            boolean isEmployer = currentUser.getId().equals(app.getJob().getEmployer().getId());

            if (!isJobSeeker && !isEmployer) {
                return ResponseEntity.badRequest().body("You can only view your own applications or applications for your jobs");
            }

            return ResponseEntity.ok(app);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Public endpoint - no authentication needed
    @GetMapping("/count-for-job/{jobId}")
    public ResponseEntity<Long> getApplicationCount(@PathVariable Long jobId) {
        long count = applicationService.getApplicationCountForJob(jobId);
        return ResponseEntity.ok(count);
    }
}

// Clean request DTOs
@Data
class ApplyJobRequest {
    private Long jobId;
    private String coverLetter;
}

@Data
class UpdateStatusRequest {
    private ApplicationStatus status;
}