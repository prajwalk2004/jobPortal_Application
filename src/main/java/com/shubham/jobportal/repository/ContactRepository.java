package com.shubham.jobportal.repository;

import com.shubham.jobportal.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findContactsByStatus(String status);

    Page<Contact> findContactsByStatus(String status, Pageable pageable);

    @Modifying
    int updateStatusById(@Param("status") String status, @Param("id") Long id,
                         @Param("updatedBy") String updatedBy);



}
