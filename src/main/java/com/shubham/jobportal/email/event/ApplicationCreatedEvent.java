package com.shubham.jobportal.email.event;

import com.shubham.jobportal.entity.JobApplication;

public record ApplicationCreatedEvent(JobApplication application) {
}
