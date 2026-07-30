package com.shubham.jobportal.contact.service;

import com.shubham.jobportal.dto.ContactRequestdto;
import com.shubham.jobportal.dto.ContactResponseDto;
import org.springframework.data.domain.Page;

public interface IcontactService {
    boolean saveContact(ContactRequestdto contactRequestdto);
    Page<ContactResponseDto> fetchNewContactMsgsWithPaginationAndSort(int pageNumber, int pageSize,
                                                                      String sortBy, String sortDir);
    boolean closeContactMsg(Long id, String status);
}
