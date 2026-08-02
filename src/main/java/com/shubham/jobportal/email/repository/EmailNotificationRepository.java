package com.shubham.jobportal.email.repository;

import com.shubham.jobportal.email.entity.EmailNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailNotificationRepository extends JpaRepository<EmailNotification , Long >
        {

}
