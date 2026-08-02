package com.shubham.jobportal.email.listner;

import com.shubham.jobportal.email.event.ApplicationCreatedEvent;
import com.shubham.jobportal.email.event.ApplicationStatusChangedEvent;
import com.shubham.jobportal.email.event.UserRegisteredEvent;
import com.shubham.jobportal.email.service.IEmailService;
import com.shubham.jobportal.entity.JobApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;
import java.util.function.Consumer;

@Component
public class EmailEventListener {

    private static final Logger log = LoggerFactory.getLogger(EmailEventListener.class);

    private final IEmailService emailService;
    private final Map<String, Consumer<JobApplication>> statusHandlers;

    public EmailEventListener(IEmailService emailService) {
        this.emailService = emailService;
        this.statusHandlers = Map.of(
                "IN_REVIEW", emailService::sendApplicationInReviewEmail,
                "INTERVIEW", emailService::sendApplicationInterviewEmail,
                "HIRED", emailService::sendApplicationHiredEmail,
                "REJECTED", emailService::sendApplicationRejectedEmail
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserRegistered(UserRegisteredEvent event) {
        emailService.sendRegistrationEmail(event.user());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onApplicationCreated(ApplicationCreatedEvent event) {
        emailService.sendApplicationReceivedEmail(event.application());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onApplicationStatusChanged(ApplicationStatusChangedEvent event) {
        Consumer<JobApplication> handler = statusHandlers.get(event.newStatus());
        if (handler == null) {
            log.info("No email configured for status transition {} -> {}", event.previousStatus(), event.newStatus());
            return;
        }
        handler.accept(event.application());
    }
}