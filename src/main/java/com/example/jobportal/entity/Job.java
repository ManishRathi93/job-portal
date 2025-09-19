package com.example.jobportal.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type")
    private JobType jobType;

    private String salary; // Optional field

    // This creates the relationship: Each job belongs to one employer
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employer_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"password", "createdAt"}) // Don't show sensitive info
    private User employer;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Custom constructor for creating jobs (without id and createdAt)
    public Job(String title, String description, String location, JobType jobType, String salary, User employer) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.jobType = jobType;
        this.salary = salary;
        this.employer = employer;
        this.createdAt = LocalDateTime.now();
    }
}