package com.shubham.jobportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditawareImp")
@EnableAsync
public class jobportalApplication {

	public static void main(String[] args) {
		SpringApplication.run(jobportalApplication.class, args);
	}

}
