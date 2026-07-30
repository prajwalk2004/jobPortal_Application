package com.shubham.jobportal.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shubham.jobportal.dto.*;
import com.shubham.jobportal.user.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
   private final IUserService userService;

    @GetMapping("/search/admin")
   public ResponseEntity<?>searchUserByEmail(@RequestParam String email){
       Optional<UserDto> userOptional=userService.searchUserByemail(email);
       if(userOptional.isEmpty()){
           return ResponseEntity.status(HttpStatus.NOT_FOUND)
                   .body(Map.of("message","user nto foundwith  this emaikl"+email));
       }
       return ResponseEntity.ok(userOptional.get());
   }

    @PatchMapping("/{userId}/role/employer/admin")
   public ResponseEntity<?>elevateToEmployer(@PathVariable Long userId){
        UserDto updatedUser = userService.elevateToEmployer(userId);
        return ResponseEntity.ok(updatedUser);
   }
    @PatchMapping("/{userId}/company/{companyId}/admin")
    public ResponseEntity<?> assignCompanyToEmployer(
            @PathVariable Long userId, @PathVariable Long companyId) {
        UserDto updatedUser = userService.assignCompanyToEmployer(userId, companyId);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping(value = "/profile/jobseeker",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProfileDto> createOrUpdateProfile(
            @RequestPart(value = "profile") String profileJson,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture,
            @RequestPart(value = "resume", required = false) MultipartFile resume,
            Authentication authentication) throws JsonProcessingException {
        String userEmail = authentication.getName();
        ProfileDto savedProfile = userService.createOrUpdateProfile(
                userEmail, profileJson, profilePicture, resume);
        return ResponseEntity.ok(savedProfile);
    }

    @GetMapping(value = "/profile/jobseeker")
    public ResponseEntity<ProfileDto> getProfile(Authentication authentication) {
        String userEmail = authentication.getName();
        ProfileDto profileDto = userService.getProfile(userEmail);
        return ResponseEntity.ok(profileDto);
    }

    @GetMapping(value = "/profile/picture/jobseeker")
    public ResponseEntity<byte[]> getProfilePicture(Authentication authentication) {
        String userEmail = authentication.getName();
       ProfileDto profileDto = userService.getProfilePicture(userEmail);
        if (profileDto == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] picture = profileDto.profilePicture();
        if (picture == null || picture.length == 0) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(profileDto.profilePictureType()));
        headers.setContentLength(picture.length);
        return new ResponseEntity<>(picture, headers, HttpStatus.OK);
    }
    @GetMapping(value = "/profile/resume/jobseeker")
    public ResponseEntity<byte[]> getResume(Authentication authentication) {
        String userEmail = authentication.getName();
       ProfileDto profileDto = userService.getResume(userEmail);
        byte[] resume = profileDto.resume();
        if (resume == null || resume.length == 0) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(profileDto.resumeType()));
        headers.setContentLength(resume.length);
        headers.setContentDispositionFormData("attachment", profileDto.resumeName());
        return new ResponseEntity<>(resume, headers, HttpStatus.OK);
    }

    @PostMapping(value = "/saved-jobs/{jobId}/jobseeker")
    public ResponseEntity<JobDto> saveJob(@PathVariable Long jobId,
                                          Authentication authentication) {
        String userEmail = authentication.getName();
        JobDto savedJob = userService.saveJob(userEmail, jobId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedJob);
    }
    @DeleteMapping(value = "/saved-jobs/{jobId}/jobseeker")
    public ResponseEntity<String> unsaveJob(@PathVariable Long jobId,
                                            Authentication authentication) {
        String userEmail = authentication.getName();
        userService.unsaveJob(userEmail, jobId);
        return ResponseEntity.status(HttpStatus.OK).body("Job unsaved successfully");
    }
    @GetMapping(value = "/saved-jobs/jobseeker")
    public ResponseEntity<List<JobDto>> getSavedJobs(Authentication authentication) {
        String userEmail = authentication.getName();
        List<JobDto> savedJobDtos = userService.getSavedJobs(userEmail);
        return ResponseEntity.ok(savedJobDtos);
    }
    @PostMapping(value = "/job-applications/jobseeker")
    public ResponseEntity<JobApplicationDto> applyForJob(
            @RequestBody @Valid ApplyJobRequestDto applyJobRequestDto, Authentication authentication) {
        String userEmail = authentication.getName();
        JobApplicationDto application = userService.applyForJob(userEmail, applyJobRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(application);
    }

    @DeleteMapping(value = "/job-applications/{jobId}/jobseeker")
    public ResponseEntity<String> withdrawApplication(@PathVariable Long jobId,
                                                      Authentication authentication) {
        String userEmail = authentication.getName();
        userService.withdrawApplication(userEmail, jobId);
        return ResponseEntity.status(HttpStatus.OK).body("Application withdrawn successfully");
    }
    @GetMapping(value = "/job-applications/jobseeker")
    public ResponseEntity<List<JobApplicationDto>> getJobSeekerApplications(Authentication authentication) {
        String userEmail = authentication.getName();
        List<JobApplicationDto> applications = userService.getJobSeekerApplications(userEmail);
        return ResponseEntity.ok(applications);
    }




}
