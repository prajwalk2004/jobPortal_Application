package com.shubham.jobportal.contact.controller;

import com.shubham.jobportal.constants.ApplicationConstants;
import com.shubham.jobportal.contact.service.IcontactService;
import com.shubham.jobportal.contact.service.serviceImplemetation.contactserviceImplementation;
import com.shubham.jobportal.dto.ContactRequestdto;
import com.shubham.jobportal.dto.ContactResponseDto;
import com.shubham.jobportal.repository.ContactRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class contactController {
    private final IcontactService contactService;
   @PostMapping("/addcontactInfo")
    public ResponseEntity<String> savecontact(@RequestBody @Valid ContactRequestdto contactRequestdto){
         boolean  is_saved=contactService.saveContact(contactRequestdto);
         if(is_saved) {
             return ResponseEntity.status(HttpStatus.CREATED)
                     .body("request processed succesfully");
         }
         else{
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                     .body("requesrt processing fail");
         }

    }
    @GetMapping
    public ResponseEntity<String>  featchOpenContact(@RequestParam   @Validated @NotBlank(message = "STATUS MUST NOT BE BLANK") String status){
       return ResponseEntity.ok().body(" contact status"+ status);
    }

    @GetMapping("/page/admin")
    public ResponseEntity<Page<ContactResponseDto>> fetchNewContactMsgsWithPaginationAndSort(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Page<ContactResponseDto> contactResponseDtoPage = contactService
                .fetchNewContactMsgsWithPaginationAndSort(pageNumber, pageSize, sortBy, sortDir);
        return ResponseEntity.status(HttpStatus.OK).body(contactResponseDtoPage);
    }

    @PatchMapping("/{id}/status/admin")
    public ResponseEntity<String> closeContactMsg(@PathVariable String id) {
        boolean isUpdated = contactService.closeContactMsg(Long.valueOf(id),
                ApplicationConstants.CLOSED_MESSAGE);
        if (isUpdated) {
            return ResponseEntity.status(HttpStatus.OK).body("Contact message updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update contact message.");
        }
    }
}

