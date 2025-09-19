package com.example.jobportal.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Each application belongs to one job
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"employer"}) // Avoid deep nesting
    private Job job;

    // Each application belongs to one job seeker
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_seeker_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"password", "createdAt"}) // Don't show sensitive info
    private User jobSeeker;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @Column(name = "applied_at")
    private LocalDateTime appliedAt = LocalDateTime.now();

    // Optional: Cover letter or message
    @Column(columnDefinition = "TEXT")
    private String coverLetter;

    // Custom constructor
    public Application(Job job, User jobSeeker, String coverLetter) {
        this.job = job;
        this.jobSeeker = jobSeeker;
        this.coverLetter = coverLetter;
        this.status = ApplicationStatus.APPLIED;
        this.appliedAt = LocalDateTime.now();
    }
}
