package com.shubham.jobportal.contact.service.serviceImplemetation;

import com.shubham.jobportal.constants.ApplicationConstants;
import com.shubham.jobportal.contact.service.IcontactService;
import com.shubham.jobportal.dto.ContactRequestdto;
import com.shubham.jobportal.dto.ContactResponseDto;
import com.shubham.jobportal.entity.Contact;
import com.shubham.jobportal.repository.ContactRepository;
import com.shubham.jobportal.util.ApplicationUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class contactserviceImplementation implements IcontactService {
    private final ContactRepository contactRepository;
    @Override
    public boolean saveContact(ContactRequestdto contactRequestdto) {
      boolean result=false;
         Contact contact=contactRepository.save(transformtoEntity(contactRequestdto));
      if(contact!=null&& contact.getId()!=null){
          result=true;
      }else{
          result=false;
      }
     return result;
    }

    public Contact transformtoEntity(ContactRequestdto contactRequestdto){
        Contact contact=new Contact();
        BeanUtils.copyProperties(contactRequestdto,contact);
        contact.setStatus("NEW");
        return contact;
    }
    @Override
    public Page<ContactResponseDto> fetchNewContactMsgsWithPaginationAndSort(
            int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Contact> contactPage = contactRepository.findContactsByStatus(
                ApplicationConstants.NEW_MESSAGE, pageable);
        Page<ContactResponseDto> responseDtoPage = contactPage.map(this::transformToDto);
        return responseDtoPage;
    }
    private ContactResponseDto transformToDto(Contact contact) {
        ContactResponseDto contactResponseDto = new ContactResponseDto(contact.getId(),
                contact.getName(), contact.getEmail(), contact.getUserType(), contact.getSubject(),
                contact.getMessage(), contact.getStatus(), contact.getCreatedAt());
        return contactResponseDto;
    }
    @Override
    public boolean closeContactMsg(Long id, String status) {
      int is_closed = contactRepository.updateStatusById(status,id, ApplicationUtility.getLoggedInUser());
//
        return is_closed>0;
    }

}
