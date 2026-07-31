package com.shubham.jobportal.job.service.imp;


import com.shubham.jobportal.dto.JobApplicationDto;
import com.shubham.jobportal.dto.JobDto;
import com.shubham.jobportal.dto.UpdateJobApplicationDto;
import com.shubham.jobportal.entity.Job;
import com.shubham.jobportal.entity.JobApplication;
import com.shubham.jobportal.entity.JobPortalUser;
import com.shubham.jobportal.job.service.IJobService;
import com.shubham.jobportal.repository.JobApplicationRepository;
import com.shubham.jobportal.repository.JobPortalUserRepository;
import com.shubham.jobportal.repository.JobRepository;
import com.shubham.jobportal.util.ApplicationUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobServiceImpl  implements IJobService {
    private  final JobRepository jobRepository;

    private final JobPortalUserRepository UserRepository;

    private final JobApplicationRepository jobApplicationRepository;


    @Override
    public List<JobDto> getEmployerJobs(String employerEmail) {
        JobPortalUser employer = UserRepository.findJobPortalUserByEmail(employerEmail).
                orElseThrow(()->new RuntimeException("Employer Not found"));
    if(employer.getCompany()==null){
        throw  new RuntimeException("Employer does not have a company assigned");

    }
    List<Job>jobs=employer.getCompany().getJobs();
     return jobs.stream().map(job -> ApplicationUtility.transformJobToDto(job))
             .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public JobDto createJob(JobDto jobDto, String employerEmail) {
       JobPortalUser employer=UserRepository.findJobPortalUserByEmail(employerEmail)
               .orElseThrow(()->new RuntimeException("Employer Not found"));
       if(employer.getCompany()==null){
           throw new RuntimeException("employer does not have company assigned");
       }
       Job job=tranformDtoToEntity(jobDto);
       job.setPostedDate(Instant.now());
       job.setApplicationsCount(0);
       job.setStatus("DRAFT");
       job.setCompany(employer.getCompany());
       Job savedJob =jobRepository.save(job);
       return  ApplicationUtility.transformJobToDto(job);

    }

    @Override
    public List<JobApplicationDto> getApplicationsByJobForEmployer(Long jobId) {
        List<JobApplication> applications = jobApplicationRepository.findByJobIdOrderByAppliedAtAsc(jobId);
        return applications.stream()
                .map(jobApplication -> ApplicationUtility.mapToJobApplicationDto(jobApplication))
                .collect(Collectors.toList());
    }


    @Transactional
    @Override
    public boolean updateJobApplication(UpdateJobApplicationDto updateJobApplicationDto) {
       int updated_row=jobApplicationRepository.updateStatusAndNotesById(updateJobApplicationDto.status().name(), updateJobApplicationDto.notes()
       , updateJobApplicationDto.applicationId(),ApplicationUtility.getLoggedInUser());
       return updated_row>0;
    }

    @Override
    public JobDto updateJobStatus(Long jobId, String status, String employerEmail) {
        if (!status.equals("ACTIVE") && !status.equals("CLOSED") && !status.equals("DRAFT")) {
            throw new RuntimeException("Invalid status. Must be ACTIVE, CLOSED, or DRAFT");
        }
        JobPortalUser employer = UserRepository.findJobPortalUserByEmail(employerEmail)
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        if (employer.getCompany() == null) {
            throw new RuntimeException("Employer does not have a company assigned");
        }
        Job job = employer.getCompany().getJobs().stream().filter(j -> j.getId().equals(jobId)).findFirst()
                .orElseThrow(() -> new RuntimeException("Job not found"));
        job.setStatus(status);
        return ApplicationUtility.transformJobToDto(job);
    }

    private Job tranformDtoToEntity(JobDto jobDto) {
        Job job = new Job();
        BeanUtils.copyProperties(jobDto, job);
        return job;
    }
}
