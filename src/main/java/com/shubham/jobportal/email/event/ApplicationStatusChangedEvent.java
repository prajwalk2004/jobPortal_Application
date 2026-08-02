package com.shubham.jobportal.email.event;

import com.shubham.jobportal.entity.JobApplication;

public record ApplicationStatusChangedEvent(JobApplication application,
                                            String previousStatus,
                                            String newStatus
) {
}
