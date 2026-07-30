package com.shubham.jobportal.user.service.Implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shubham.jobportal.constants.ApplicationConstants;
import com.shubham.jobportal.dto.*;
import com.shubham.jobportal.entity.*;
import com.shubham.jobportal.repository.*;
import com.shubham.jobportal.user.service.IUserService;
import com.shubham.jobportal.util.ApplicationUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements IUserService {
    private final JobPortalUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;
    private final ProfileRepository profileRepository;
    private final JobApplicationRepository jobApplicationRepository;

    @Override
    public Optional<UserDto> searchUserByemail(String email) {
             return  userRepository.findJobPortalUserByEmail(email).map(this::mapToUserDto);
    }

    @Override
    @Transactional
    public UserDto elevateToEmployer(Long userId) {
        JobPortalUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (ApplicationConstants.ROLE_EMPLOYER.equals(user.getRole().getName())) {
            return mapToUserDto(user);
        }
        if (ApplicationConstants.ROLE_ADMIN.equals(user.getRole().getName())) {
            throw new RuntimeException("Cannot elevate admin user to employer role");
        }


        Role employerRole = roleRepository.findRoleByName(ApplicationConstants.ROLE_EMPLOYER)
                .orElseThrow(() -> new RuntimeException("ROLE_EMPLOYER not found"));
        user.setRole(employerRole);


        return mapToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto assignCompanyToEmployer(Long userId, Long companyId) {
        JobPortalUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (!ApplicationConstants.ROLE_EMPLOYER.equals(user.getRole().getName())) {
            throw new RuntimeException("User must be an employer to be assigned to a company");
        }
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));
        user.setCompany(company);

        return mapToUserDto(user);
    }

    @Override
    @Transactional
    public ProfileDto createOrUpdateProfile(String userEmail, String profileJson, MultipartFile profilePicture, MultipartFile resume)
            throws JsonProcessingException {
        JobPortalUser user=userRepository.findJobPortalUserByEmail(userEmail).
                orElseThrow(()->new RuntimeException("User not found with  this email"+userEmail));
        Profile profile=user.getProfile();
        if(profile==null){
            profile=new Profile();
            profile.setUser(user);
        }
        ObjectMapper objectMapper=new ObjectMapper();
        ProfileDto profileDto=objectMapper.readValue(profileJson,ProfileDto.class);
        Profile saved_profile=profileRepository.save(mapToProfile(profile,profileDto,profilePicture,resume));
         return mapToProfileDto(saved_profile,false);

    }
    @Override
    public ProfileDto getProfile(String userEmail) {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        if (user.getProfile() == null) {
            return null;
        }
        return mapToProfileDto(user.getProfile(), false);
    }

    @Override
    public ProfileDto getProfilePicture(String userEmail) {
        JobPortalUser user=userRepository.findJobPortalUserByEmail(userEmail).
                orElseThrow(()->new RuntimeException("User not found with this email"+userEmail));
        if(user.getProfile()==null){
            return null;
        }

        return  mapToProfileDto(user.getProfile(),true);
    }

    @Override
    public ProfileDto getResume(String userEmail) {
        JobPortalUser user=userRepository.findJobPortalUserByEmail(userEmail).
                 orElseThrow(()->new RuntimeException("User is not found with this email info"+userEmail));
        if(user.getProfile()==null){
            return null;
        }
        return mapToProfileDto(user.getProfile(),true);
    }

    @Transactional
    @Override
    public JobDto saveJob(String userEmail, Long jobId) {
        JobPortalUser user=userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(()->new RuntimeException("User Not found with Email"+userEmail));

        Job job=jobRepository.findById(jobId)
                .orElseThrow(()-> new RuntimeException("Job not found with ID: " + jobId));
        user.getSavedJobs().add(job);
        return ApplicationUtility.transformJobToDto(job);

    }
    @Transactional
    @Override
    public void unsaveJob(String userEmail, Long jobId) {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));
        user.getSavedJobs().remove(job);
    }
    @Override
    public List<JobDto> getSavedJobs(String userEmail) {

        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        return user.getSavedJobs().stream().map(job -> ApplicationUtility.transformJobToDto(job))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public JobApplicationDto applyForJob(String userEmail, ApplyJobRequestDto applyJobRequestDto) {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        Long jobId = applyJobRequestDto.jobId();
        if (jobApplicationRepository.existsByUserIdAndJobId(user.getId(), jobId)) {
            throw new RuntimeException("You have already applied for this job");
        }
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));
        JobApplication application = new JobApplication();
        application.setUser(user);
        application.setJob(job);
        application.setAppliedAt(Instant.now());
        application.setStatus(ApplicationConstants.PENDING);
        application.setCoverLetter(applyJobRequestDto.coverLetter());
        JobApplication saved = jobApplicationRepository.save(application);
        job.setApplicationsCount(job.getApplicationsCount() != null ? job.getApplicationsCount() + 1 : 1);
        return ApplicationUtility.mapToJobApplicationDto(saved);
    }
    @Transactional
    @Override
    public void withdrawApplication(String userEmail, Long jobId) {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        if (!jobApplicationRepository.existsByUserIdAndJobId(user.getId(), jobId)) {
            throw new RuntimeException("You have not applied for this job");
        }
        jobApplicationRepository.deleteByUserIdAndJobId(user.getId(), jobId);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));

        if (job.getApplicationsCount() != null && job.getApplicationsCount() > 0) {
            job.setApplicationsCount(job.getApplicationsCount() - 1);

        }
    }

    @Override
    public List<JobApplicationDto> getJobSeekerApplications(String userEmail) {

        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        return user.getJobApplications().stream().map(jobApp ->
                        ApplicationUtility.mapToJobApplicationDto(jobApp))
                .collect(Collectors.toList());
    }


    private UserDto mapToUserDto(JobPortalUser user) {
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(user, dto);
        dto.setUserId(user.getId());
        dto.setRole(user.getRole() != null ? user.getRole().getName() : null);
        dto.setCompanyId(user.getCompany() != null ? user.getCompany().getId() : null);
        dto.setCompanyName(user.getCompany() != null ? user.getCompany().getName() : null);
        return dto;
    }

    private Profile mapToProfile(Profile profile, ProfileDto profileDto,
                                 MultipartFile profilePicture, MultipartFile resume) {

        profile.setJobTitle(profileDto.jobTitle());
        profile.setLocation(profileDto.location());
        profile.setExperienceLevel(profileDto.experienceLevel());
        profile.setProfessionalBio(profileDto.professionalBio());
        profile.setPortfolioWebsite(profileDto.portfolioWebsite());

        if (profilePicture != null && !profilePicture.isEmpty()) {
            try {
                profile.setProfilePicture(profilePicture.getBytes());
                profile.setProfilePictureName(profilePicture.getOriginalFilename());
                profile.setProfilePictureType(profilePicture.getContentType());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload profile picture", e);
            }
        }

        if (resume != null && !resume.isEmpty()) {
            try {
                profile.setResume(resume.getBytes());
                profile.setResumeName(resume.getOriginalFilename());
                profile.setResumeType(resume.getContentType());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload resume", e);
            }
        }
        return profile;
    }
    private ProfileDto mapToProfileDto(Profile profile, boolean includeBinaryData) {
        ProfileDto dto;
        if (includeBinaryData) {
            dto = new ProfileDto(profile.getId(), profile.getUser().getId(),
                    profile.getJobTitle(), profile.getLocation(), profile.getExperienceLevel(),
                    profile.getProfessionalBio(), profile.getPortfolioWebsite(), profile.getProfilePicture(),
                    profile.getProfilePictureName(), profile.getProfilePictureType(), profile.getResume(),
                    profile.getResumeName(), profile.getResumeType(), profile.getCreatedAt(), profile.getUpdatedAt()
            );
        } else {
            dto = new ProfileDto(profile.getId(), profile.getUser().getId(),
                    profile.getJobTitle(), profile.getLocation(), profile.getExperienceLevel(),
                    profile.getProfessionalBio(), profile.getPortfolioWebsite(), null,
                    profile.getProfilePictureName(), profile.getProfilePictureType(), null,
                    profile.getResumeName(), profile.getResumeType(), profile.getCreatedAt(), profile.getUpdatedAt());
        }
        return dto;
    }



}
