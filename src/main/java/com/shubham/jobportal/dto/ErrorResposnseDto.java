package com.shubham.jobportal.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorResposnseDto(String apiPath, HttpStatus errorCode, String errormessage,
                                LocalDateTime rrorTime) {
}
