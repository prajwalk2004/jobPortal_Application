package com.shubham.jobportal.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shubham.jobportal.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    Optional<UserDto> searchUserByemail(String email);
     UserDto elevateToEmployer(Long userId);
    UserDto assignCompanyToEmployer(Long userId, Long companyId);

    ProfileDto createOrUpdateProfile(String userEmail, String profileJson,
                                     MultipartFile profilePicture, MultipartFile resume) throws JsonProcessingException;

    ProfileDto getProfile(String userEmail);
    ProfileDto getProfilePicture(String userEmail);
    ProfileDto getResume(String userEmail);

    JobDto saveJob(String userEmail, Long jobId);
    void unsaveJob(String userEmail, Long jobId);
    List<JobDto> getSavedJobs(String userEmail);
    JobApplicationDto applyForJob(String userEmail, ApplyJobRequestDto request);
    void withdrawApplication(String userEmail, Long jobId);

    List<JobApplicationDto> getJobSeekerApplications(String userEmail);

}
