package com.shubham.jobportal.email.entity;

import com.shubham.jobportal.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "email_notifications")
public class EmailNotification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "recipient_email", nullable = false, length = 255)
    private String recipientEmail;

    @NotNull
    @Column(name = "subject", nullable = false, length = 255)
    private String subject;

    @NotNull
    @Column(name = "template_name", nullable = false, length = 100)
    private String templateName;

    @NotNull
    @Column(name = "notification_type", nullable = false, length = 50)
    private String notificationType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EmailNotificationStatus status;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Lob
    @Column(name = "error_message")
    private String errorMessage;
}
