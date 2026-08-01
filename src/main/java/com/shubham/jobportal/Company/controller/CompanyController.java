package com.shubham.jobportal.Company.controller;

import com.shubham.jobportal.Company.service.IcompanyService;
import com.shubham.jobportal.dto.Companydto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {
 private final IcompanyService icompanyService;

   private final IcompanyService companyService;


    @GetMapping(path = "/public")
    public ResponseEntity<List<Companydto> >getallCompanies(){
       List<Companydto> companyList= companyService.getallCompany();
        return ResponseEntity.ok().body(companyList);
    }
    @PostMapping("/admin")
    public ResponseEntity<String>createCompany(@RequestBody @Valid Companydto companydto){
        boolean is_created=companyService.createCompany(companydto);
        if (is_created) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Request processed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Request processing failed");
        }
    }
    @GetMapping(path = "/admin")
    public ResponseEntity<List<Companydto>>getAllCompaniesforAdmin(){
        List<Companydto>companyList=companyService.getAllCompaniesForAdmin();
        return ResponseEntity.ok().body(companyList);
    }
    @PutMapping(path = "/{id}/admin")
    public ResponseEntity<String> updateCompanyDetails(@PathVariable @NotBlank String id,
                                                       @RequestBody @Valid Companydto companyDto) {
        boolean isUpdated = companyService.updateCompanyDetails(Long.valueOf(id),companyDto);
        if (isUpdated) {
            return ResponseEntity.status(HttpStatus.OK).body("Company details updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update Company details");
        }
    }
    @DeleteMapping(path = "/{id}/admin", version = "1.0")
    public ResponseEntity<String> deleteCompanyById(@PathVariable @NotBlank String id) {
        companyService.deleteCompanyById(Long.valueOf(id));
        return ResponseEntity.status(HttpStatus.OK).body("Company record deleted successfully.");
    }
}
