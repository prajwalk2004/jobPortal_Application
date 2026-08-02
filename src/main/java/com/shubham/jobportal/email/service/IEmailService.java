package com.shubham.jobportal.email.service;

import com.shubham.jobportal.entity.JobApplication;
import com.shubham.jobportal.entity.JobPortalUser;

public interface IEmailService {
    void sendRegistrationEmail(JobPortalUser user);

    void sendApplicationReceivedEmail(JobApplication application);

    void sendApplicationInReviewEmail(JobApplication application);

    void sendApplicationInterviewEmail(JobApplication application);

    void sendApplicationHiredEmail(JobApplication application);

    void sendApplicationRejectedEmail(JobApplication application);

}
