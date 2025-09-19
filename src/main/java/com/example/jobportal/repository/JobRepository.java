package com.example.jobportal.repository;


import com.example.jobportal.entity.Job;
import com.example.jobportal.entity.JobType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    // Find jobs by employer
    List<Job> findByEmployerId(Long employerId);

    // Find jobs by job type
    List<Job> findByJobType(JobType jobType);

    // Search jobs by title (case-insensitive)
    List<Job> findByTitleContainingIgnoreCase(String title);

    // Search jobs by location (case-insensitive)
    List<Job> findByLocationContainingIgnoreCase(String location);

    // Custom query to search by title OR location
    @Query("SELECT j FROM Job j WHERE " +
            "LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(j.location) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Job> searchJobs(@Param("keyword") String keyword);
}