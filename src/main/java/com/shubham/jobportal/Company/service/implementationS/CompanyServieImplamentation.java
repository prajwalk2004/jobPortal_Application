package com.shubham.jobportal.Company.service.implementationS;

import com.shubham.jobportal.Company.service.IcompanyService;
import com.shubham.jobportal.constants.ApplicationConstants;
import com.shubham.jobportal.dto.Companydto;
import com.shubham.jobportal.dto.JobDto;
import com.shubham.jobportal.entity.Company;
import com.shubham.jobportal.entity.Job;
import com.shubham.jobportal.repository.CompanyRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CompanyServieImplamentation implements IcompanyService {
  private  final CompanyRepository companyRepository;
     @Autowired
    public CompanyServieImplamentation(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public List<Companydto> getallCompany() {
        List<Company> companyList =companyRepository.fetchCompaniesWithJobsByStatus(ApplicationConstants.ACTIVE_STATUS);
          List<Companydto>newCompany_list= companyList.stream().map(this::transformTocompanyDTO).collect(Collectors.toList());
         return newCompany_list;
    }

    @Override
    @Transactional
    public boolean createCompany(Companydto companyDto) {
       Company company=transformcompanyDtotoEntity(companyDto);
        Company savedCompany=companyRepository.save(company);
        return savedCompany.getId() != null && savedCompany.getId() > 0;
    }

    @Override
    public List<Companydto> getAllCompaniesForAdmin() {
        List<Company> company=companyRepository.findAll();
        return company.stream().map(this::transformCompanyToDtoForAdmin).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean updateCompanyDetails(Long id, Companydto companyDto) {
        int updatedRecords = companyRepository.updateCompanyDetails(
                id,companyDto.name(),companyDto.logo(),
                companyDto.industry(),companyDto.size(),companyDto.rating(),
                companyDto.locations(),companyDto.founded(),companyDto.description(),
                companyDto.employees(),companyDto.website()
        );
        return updatedRecords > 0;
    }

    @Override
    @Transactional
    public void deleteCompanyById(Long id) {
        companyRepository.deleteById(id);
    }

    private Companydto transformCompanyToDtoForAdmin(Company company) {
        return new Companydto(company.getId(), company.getName(), company.getLogo(),
                company.getIndustry(), company.getSize(), company.getRating(),
                company.getLocations(), company.getFounded(), company.getDescription(),
                company.getEmployees(), company.getWebsite(), company.getCreatedAt(),null);
    }


    private Company transformcompanyDtotoEntity(Companydto companyDto) {
         Company company=new Company();
        BeanUtils.copyProperties(companyDto,company);
        return company;
    }

    private Companydto transformTocompanyDTO(Company company){
        List<JobDto> jobDtos = company.getJobs().stream()
                .map(this::transformJobToDto)
                .collect(Collectors.toList());
        return new Companydto(company.getId(), company.getName(), company.getLogo(),
                company.getIndustry(), company.getSize(), company.getRating(),
                company.getLocations(), company.getFounded(), company.getDescription(),
                company.getEmployees(), company.getWebsite(), company.getCreatedAt(),jobDtos);
    }
    private JobDto transformJobToDto(Job job) {
        return new JobDto(
                job.getId(),
                job.getTitle(),
                job.getCompany().getId(),
                job.getCompany().getName(),
                job.getCompany().getLogo(),
                job.getLocation(),
                job.getWorkType(),
                job.getJobType(),
                job.getCategory(),
                job.getExperienceLevel(),
                job.getSalaryMin(),
                job.getSalaryMax(),
                job.getSalaryCurrency(),
                job.getSalaryPeriod(),
                job.getDescription(),
                job.getRequirements(),
                job.getBenefits(),
                job.getPostedDate(),
                job.getApplicationDeadline(),
                job.getApplicationsCount(),
                job.getFeatured(),
                job.getUrgent(),
                job.getRemote(),
                job.getStatus()
        );
    }
}
