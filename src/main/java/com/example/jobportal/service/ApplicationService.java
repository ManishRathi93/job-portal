package com.example.jobportal.service;


import com.example.jobportal.entity.Application;
import com.example.jobportal.entity.ApplicationStatus;
import com.example.jobportal.entity.Job;
import com.example.jobportal.entity.User;
import com.example.jobportal.entity.UserType;
import com.example.jobportal.repository.ApplicationRepository;
import com.example.jobportal.repository.JobRepository;
import com.example.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public Application applyForJob(Long jobId, Long jobSeekerId, String coverLetter) {
        // Check if job exists
        Optional<Job> job = jobRepository.findById(jobId);
        if (job.isEmpty()) {
            throw new RuntimeException("Job not found!");
        }

        // Check if a user exists and is a jobseeker
        Optional<User> jobSeeker = userRepository.findById(jobSeekerId);
        if (jobSeeker.isEmpty()) {
            throw new RuntimeException("User not found!");
        }
        if (jobSeeker.get().getUserType() != UserType.JOB_SEEKER) {
            throw new RuntimeException("Only job seekers can apply for jobs!");
        }

        // Check if user already applied for this job
        Optional<Application> existingApplication = applicationRepository.findByJobIdAndJobSeekerId(jobId, jobSeekerId);
        if (existingApplication.isPresent()) {
            throw new RuntimeException("You have already applied for this job!");
        }

        // Prevent applying to own job (if somehow an employer is marked as jobseeker)
        if (job.get().getEmployer().getId().equals(jobSeekerId)) {
            throw new RuntimeException("You cannot apply to your own job!");
        }

        // Create and save application
        Application application = new Application(job.get(), jobSeeker.get(), coverLetter);
        return applicationRepository.save(application);
    }

    public List<Application> getMyApplications(Long jobSeekerId) {
        return applicationRepository.findByJobSeekerId(jobSeekerId);
    }

    public List<Application> getApplicationsForJob(Long jobId, Long employerId) {
        // Verify that the employer owns this job
        Optional<Job> job = jobRepository.findById(jobId);
        if (job.isEmpty()) {
            throw new RuntimeException("Job not found!");
        }
        if (!job.get().getEmployer().getId().equals(employerId)) {
            throw new RuntimeException("You can only view applications for your own jobs!");
        }

        return applicationRepository.findByJobId(jobId);
    }

    public List<Application> getAllApplicationsForEmployer(Long employerId) {
        // Verify user is an employer
        Optional<User> employer = userRepository.findById(employerId);
        if (employer.isEmpty() || employer.get().getUserType() != UserType.EMPLOYER) {
            throw new RuntimeException("Only employers can view applications!");
        }

        return applicationRepository.findApplicationsForEmployerJobs(employerId);
    }

    public Application updateApplicationStatus(Long applicationId, ApplicationStatus newStatus, Long employerId) {
        Optional<Application> application = applicationRepository.findById(applicationId);
        if (application.isEmpty()) {
            throw new RuntimeException("Application not found!");
        }

        // Verify that the employer owns the job this application is for
        if (!application.get().getJob().getEmployer().getId().equals(employerId)) {
            throw new RuntimeException("You can only update applications for your own jobs!");
        }

        application.get().setStatus(newStatus);
        return applicationRepository.save(application.get());
    }

    public long getApplicationCountForJob(Long jobId) {
        return applicationRepository.countByJobId(jobId);
    }

    public Optional<Application> getApplicationById(Long applicationId) {
        return applicationRepository.findById(applicationId);
    }
}