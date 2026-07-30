package com.shubham.jobportal.dto;

import jakarta.validation.constraints.*;

import java.io.Serializable;

public record ContactRequestdto(
        @NotBlank(message = "email cannot be null")
        @Email(message = "Invalid email is provided")
        String email,
        @NotBlank(message = "message cannot be empty")
        @Size(min = 10,max = 500,message = "message must be between 1- to 50 character")
        String message,
        @NotBlank(message = "name cannot be empty")
        @Size(min = 5,max = 30,message = "name but be at least 5 character")
        String name,
        @NotBlank(message = "subject cannot be empty")
        @Size(min = 10,max = 500,message = "subject must contain at least 10 characte")
        String subject,
        @NotBlank(message = "userType cannot be epmty")
        @Pattern(regexp = "Job Seeker|Employer|Other",message = "User must be one of :Job Seeker,Employer,Other")
        String userType) implements Serializable {
}
