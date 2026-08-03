package com.shubham.jobportal.email.service.Implementation;

import com.shubham.jobportal.email.entity.EmailNotification;
import com.shubham.jobportal.email.entity.EmailNotificationStatus;
import com.shubham.jobportal.email.repository.EmailNotificationRepository;
import com.shubham.jobportal.email.service.IEmailService;
import com.shubham.jobportal.entity.Company;
import com.shubham.jobportal.entity.Job;
import com.shubham.jobportal.entity.JobApplication;
import com.shubham.jobportal.entity.JobPortalUser;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy").withZone(ZoneId.systemDefault());

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final EmailNotificationRepository emailNotificationRepository;

    @Value("${app.name:Tech Forage}")
    private String applicationName;

    @Value("${app.mail.from:no-reply@techforage.com}")
    private String fromAddress;

    @Value("${app.login-url:http://localhost:5173/login}")
    private String loginUrl;

    @Override
    @Async
    public void sendRegistrationEmail(JobPortalUser user) {
        Context context = new Context();
        context.setVariable("userName", user.getName());
        context.setVariable("email", user.getEmail());
        context.setVariable("role", user.getRole() != null ? user.getRole().getName() : "");
        context.setVariable("registrationDate", format(user.getCreatedAt()));
        context.setVariable("applicationName", applicationName);
        context.setVariable("loginUrl", loginUrl);

        send(user.getEmail(), "Welcome to " + applicationName + "!", "registration-success", "REGISTRATION", context);
    }

    @Override
    @Async
    public void sendApplicationReceivedEmail(JobApplication application) {
        send(applicationEmail(application, application.getStatus(), "Application received",
                "application-received", "APPLICATION_RECEIVED"));
    }

    @Override
    @Async
    public void sendApplicationInReviewEmail(JobApplication application) {
        send(applicationEmail(application, "IN_REVIEW", "Your application is under review",
                "application-in-review", "APPLICATION_IN_REVIEW"));
    }

    @Override
    @Async
    public void sendApplicationInterviewEmail(JobApplication application) {
        send(applicationEmail(application, "INTERVIEW", "You've been selected for an interview",
                "application-interview", "APPLICATION_INTERVIEW"));
    }

    @Override
    @Async
    public void sendApplicationHiredEmail(JobApplication application) {
        send(applicationEmail(application, "HIRED", "Congratulations - you're hired!",
                "application-hired", "APPLICATION_HIRED"));
    }

    @Override
    @Async
    public void sendApplicationRejectedEmail(JobApplication application) {
        send(applicationEmail(application, "REJECTED", "Update on your application",
                "application-rejected", "APPLICATION_REJECTED"));
    }

    private record PreparedEmail(String to, String subject, String template, String type, Context context) {
    }

   /*
   few thing need to managed --- which not working properly, config file as well...
    */
    private PreparedEmail applicationEmail(JobApplication application, String statusForDisplay,
                                           String subject, String template, String type) {
        Job job = application.getJob();
        Company company = job.getCompany();
        JobPortalUser candidate = application.getUser();

        Context context = new Context();
        context.setVariable("candidateName", candidate.getName());
        context.setVariable("candidateEmail", candidate.getEmail());
        context.setVariable("jobTitle", job.getTitle());
        context.setVariable("jobLocation", job.getLocation());
        context.setVariable("employmentType", job.getJobType());
        context.setVariable("applicationId", "APP-" + application.getId());
        context.setVariable("applicationDate", format(application.getAppliedAt()));
        context.setVariable("applicationStatus", statusForDisplay);
        context.setVariable("statusUpdateDate", format(Instant.now()));
        context.setVariable("companyName", company.getName());
        context.setVariable("companyLogo", company.getLogo());
        context.setVariable("companyWebsite", company.getWebsite());
        context.setVariable("companyEmail", fromAddress);
        context.setVariable("companyAddress", company.getLocations());
        context.setVariable("applicationName", applicationName);

        return new PreparedEmail(candidate.getEmail(), subject, template, type, context);
    }

    private void send(PreparedEmail email) {
        send(email.to(), email.subject(), email.template(), email.type(), email.context());
    }

    private void send(String to, String subject, String templateName, String notificationType, Context context) {
        EmailNotification notification = new EmailNotification();
        notification.setRecipientEmail(to);
        notification.setSubject(subject);
        notification.setTemplateName(templateName);
        notification.setNotificationType(notificationType);

        try {
            String html = templateEngine.process("email/" + templateName, context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            helper.setFrom(fromAddress);

            mailSender.send(message);

            notification.setStatus(EmailNotificationStatus.SENT);
            notification.setSentAt(Instant.now());
            log.info("Email sent successfully to {} using template {}", to, templateName);
        } catch (Exception ex) {
            notification.setStatus(EmailNotificationStatus.FAILED);
            notification.setErrorMessage(ex.getMessage());
            log.error("Failed to send email to {} using template {}: {}", to, templateName, ex.getMessage());
        } finally {
            try {
                emailNotificationRepository.save(notification);
            } catch (Exception persistEx) {
                log.error("Failed to persist email notification log for {}: {}", to, persistEx.getMessage());
            }
        }
    }

    private String format(Instant instant) {
        return instant != null ? DATE_FORMAT.format(instant) : "";
    }
}