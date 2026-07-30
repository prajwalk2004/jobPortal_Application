package com.shubham.jobportal.auth;

import com.shubham.jobportal.constants.ApplicationConstants;
import com.shubham.jobportal.dto.LoginRequestDto;
import com.shubham.jobportal.dto.LoginResponseDto;
import com.shubham.jobportal.dto.RegisterRequestDto;
import com.shubham.jobportal.dto.UserDto;
import com.shubham.jobportal.entity.JobPortalUser;
import com.shubham.jobportal.entity.Role;
import com.shubham.jobportal.repository.JobPortalUserRepository;
import com.shubham.jobportal.repository.RoleRepository;
import com.shubham.jobportal.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final JobPortalUserRepository jobPortalUserRepository;
    private final RoleRepository roleRepository;

    private final JwtUtil jwtUtil;
   private final CompromisedPasswordChecker compromisedPasswordChecker;

    @PostMapping(value = "/login/public")
    public ResponseEntity<LoginResponseDto> apiLogin(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            var resultAuthentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.username(),
                    loginRequestDto.password()));
             String jwtToken=jwtUtil.generateJwtToken(resultAuthentication);
            var userDto = new UserDto();
            var loggedInUser = (JobPortalUser) resultAuthentication.getPrincipal();
            BeanUtils.copyProperties(loggedInUser, userDto);
            userDto.setRole(loggedInUser.getRole().getName());
            userDto.setUserId(loggedInUser.getId());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new LoginResponseDto(HttpStatus.OK.getReasonPhrase(),
                            userDto, jwtToken));
        } catch (BadCredentialsException ex) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED,
                    "Invalid username or password");
        } catch (AuthenticationException ex) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED,
                    "Authentication failed");
        } catch (Exception ex) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred");
        }

    }

    @PostMapping("/register/public")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDto registerRequestDto){
        CompromisedPasswordDecision decision = compromisedPasswordChecker
                .check(registerRequestDto.password());
        if (decision.isCompromised()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("password", "Choose a strong password"));
        }
        Optional<JobPortalUser> existingUser = jobPortalUserRepository.readUserByEmailOrMobileNumber
                (registerRequestDto.email(), registerRequestDto.mobileNumber());
        if (existingUser.isPresent()) {
            Map<String, String> errors = new HashMap<>();
            JobPortalUser jobPortalUser = existingUser.get();
            if (jobPortalUser.getEmail().equalsIgnoreCase(registerRequestDto.email())) {
                errors.put("email", "Email is already registered");
            }
            if (jobPortalUser.getMobileNumber().equals(registerRequestDto.mobileNumber())) {
                errors.put("mobileNumber", "Mobile number is already registered");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        JobPortalUser jobPortalUser = new JobPortalUser();
        BeanUtils.copyProperties(registerRequestDto, jobPortalUser);
        jobPortalUser.setPasswordHash(passwordEncoder.encode(registerRequestDto.password()));
        Role role = roleRepository.findRoleByName(ApplicationConstants.ROLE_JOB_SEEKER)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " +
                        ApplicationConstants.ROLE_JOB_SEEKER));
        jobPortalUser.setRole(role);
        jobPortalUserRepository.save(jobPortalUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }
    private ResponseEntity<LoginResponseDto> buildErrorResponse(HttpStatus status,
                                                                String message) {
        return ResponseEntity
                .status(status)
                .body(new LoginResponseDto(message, null, null));
    }

}