package com.shubham.jobportal.email.event;

import com.shubham.jobportal.entity.JobPortalUser;

public record UserRegisteredEvent(JobPortalUser user) {
}
