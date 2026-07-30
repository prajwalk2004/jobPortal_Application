package com.shubham.jobportal.audit;

import com.shubham.jobportal.util.ApplicationUtility;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;
 @Component("auditawareImp")
public class AuditAwareImplem implements AuditorAware {
    @Override
    public Optional getCurrentAuditor() {
        return Optional.of(ApplicationUtility.getLoggedInUser());
    }
}
