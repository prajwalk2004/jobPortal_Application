# TechForage – Job Recruitment & Application Management System

> **Stop Searching. Start Foraging.**

TechForage is a full-stack job recruitment and application management system that connects job seekers with recruiters through a centralized platform.

The application provides role-based functionality for **Job Seekers, Recruiters, and Administrators**, allowing users to manage profiles, post jobs, apply for jobs, track applications, and receive email notifications.

---

## 🚀 Features

### 👤 Job Seeker

- Register and log in securely
- Create and update profile
- Upload resume
- Upload profile photo
- Browse available job opportunities
- Search and apply for jobs
- Track application status
- Receive email notifications for application updates

### 🏢 Recruiter / Employer

- Register as a recruiter
- Create and manage job postings
- Specify required skills and job details
- View applications for posted jobs
- Update candidate application status
- Send application status updates through email

### 🔐 Admin

- Role-based access to administrative functionality
- Manage users and application data
- Control access to protected resources

### 📧 Email Notifications

The application sends email notifications for important events such as:

- User registration
- Job application
- Application status updates
- Interview updates
- Hiring/rejection notifications

---

## 🛠️ Tech Stack

### Backend
- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- Spring Security
- JWT Authentication
- REST APIs
- JavaMailSender
- Thymeleaf

### Frontend
- React.js
- HTML
- CSS
- JavaScript

### Database
- MySQL

### Development & Testing
- Maven
- Docker
- Postman
- Git & GitHub

---

## 🏗️ Application Architecture

```text
                    ┌─────────────────────┐
                    │     React Client    │
                    │      Frontend       │
                    └──────────┬──────────┘
                               │
                               │ REST API
                               ▼
                    ┌─────────────────────┐
                    │    Spring Boot      │
                    │      Backend        │
                    │                     │
                    │  ┌───────────────┐  │
                    │  │ Spring        │  │
                    │  │ Security + JWT│  │
                    │  └───────────────┘  │
                    │                     │
                    │  REST Controllers   │
                    │        ↓            │
                    │  Service Layer      │
                    │        ↓            │
                    │  JPA / Hibernate   │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │       MySQL         │
                    │      Database       │
                    └─────────────────────┘

                               │
                               ▼
                    ┌─────────────────────┐
                    │    SMTP / Email     │
                    │     Service         │
                    └─────────────────────┘
