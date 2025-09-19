package com.example.jobportal.repository;


import com.example.jobportal.entity.Application;
import com.example.jobportal.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // Find all applications for a specific job
    List<Application> findByJobId(Long jobId);

    // Find all applications by a specific jobseeker
    List<Application> findByJobSeekerId(Long jobSeekerId);

    // Check if a user already applied for a job (prevent duplicate applications)
    Optional<Application> findByJobIdAndJobSeekerId(Long jobId, Long jobSeekerId);

    // Find applications by status
    List<Application> findByStatus(ApplicationStatus status);

    // Find applications for jobs posted by a specific employer
    @Query("SELECT a FROM Application a WHERE a.job.employer.id = :employerId")
    List<Application> findApplicationsForEmployerJobs(@Param("employerId") Long employerId);

    // Count applications for a specific job
    long countByJobId(Long jobId);
}