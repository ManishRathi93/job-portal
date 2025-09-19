package com.example.jobportal.service;


import com.example.jobportal.entity.Job;
import com.example.jobportal.entity.JobType;
import com.example.jobportal.entity.User;
import com.example.jobportal.entity.UserType;
import com.example.jobportal.repository.JobRepository;
import com.example.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public Job createJob(Job job, Long employerId) {
        // Check if the employer exists and is actually an employer
        Optional<User> employer = userRepository.findById(employerId);
        if (employer.isEmpty()) {
            throw new RuntimeException("Employer not found!");
        }
        if (employer.get().getUserType() != UserType.EMPLOYER) {
            throw new RuntimeException("Only employers can create jobs!");
        }

        job.setEmployer(employer.get());
        return jobRepository.save(job);
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Optional<Job> getJobById(Long id) {
        return jobRepository.findById(id);
    }

    public List<Job> getJobsByEmployer(Long employerId) {
        return jobRepository.findByEmployerId(employerId);
    }

    public List<Job> searchJobs(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllJobs();
        }
        return jobRepository.searchJobs(keyword.trim());
    }

    public List<Job> getJobsByType(JobType jobType) {
        return jobRepository.findByJobType(jobType);
    }

    public Job updateJob(Long jobId, Job updatedJob, Long employerId) {
        Optional<Job> existingJob = jobRepository.findById(jobId);
        if (existingJob.isEmpty()) {
            throw new RuntimeException("Job not found!");
        }

        // Check if the person trying to update is the job owner
        if (!existingJob.get().getEmployer().getId().equals(employerId)) {
            throw new RuntimeException("You can only update your own jobs!");
        }

        Job job = existingJob.get();
        job.setTitle(updatedJob.getTitle());
        job.setDescription(updatedJob.getDescription());
        job.setLocation(updatedJob.getLocation());
        job.setJobType(updatedJob.getJobType());
        job.setSalary(updatedJob.getSalary());

        return jobRepository.save(job);
    }

    public void deleteJob(Long jobId, Long employerId) {
        Optional<Job> existingJob = jobRepository.findById(jobId);
        if (existingJob.isEmpty()) {
            throw new RuntimeException("Job not found!");
        }

        // Check if the person trying to delete is the job owner
        if (!existingJob.get().getEmployer().getId().equals(employerId)) {
            throw new RuntimeException("You can only delete your own jobs!");
        }

        jobRepository.deleteById(jobId);
    }
}
