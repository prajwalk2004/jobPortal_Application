package com.shubham.jobportal.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PathsConfig {

    @Bean(name = "publicPaths")
    public List<String> publicPaths() {
        return List.of(

                "/webjars/**",
                "/api/contacts/public",
                "/api/auth/login/public",
                "/api/companies/public",
                "/api/auth/register/public",
                "/api/csrf-token/public"
        );
    }

    @Bean(name = "securedPaths")
    public List<String> securedPaths() {
        return List.of(
                "/api/**"
        );
    }
    @Bean(name = "adminPaths")
    public List<String> adminPaths() {
        return List.of(
                "/api/contacts/admin",
                "/api/contacts/sort/admin",
                "/api/contacts/page/admin",
                "/api/contacts/${id}/status/admin"
        );
    }

    @Bean(name = "employerPaths")
    public List<String> employerPaths() {
        return List.of(
                "/api/jobs/employer",
                "/api/jobs/${jobId}/status/employer"
        );
    }

    @Bean(name = "jobseekerPaths")
    public List<String> jobseekerPaths() {
        return List.of(
                "/api/users/profile/jobseeker",
                "/api/users/profile/picture/jobseeker",
                "/api/users/profile/resume/jobseeker",
                "/api/users/saved-jobs/${jobId}/jobseeker",
                "/api/users/saved-jobs/jobseeker",
                "/api/users/job-applications/jobseeker",
                "/api/users/job-applications/${jobId}/jobseeker"
        );
    }

}