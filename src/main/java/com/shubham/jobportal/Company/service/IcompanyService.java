package com.shubham.jobportal.Company.service;

import com.shubham.jobportal.dto.Companydto;

import java.util.List;

public interface IcompanyService {
    List<Companydto>getallCompany();
    boolean createCompany(Companydto companyDto);
    List<Companydto> getAllCompaniesForAdmin();
    boolean updateCompanyDetails(Long id, Companydto companyDto);
    void deleteCompanyById(Long id);
}
