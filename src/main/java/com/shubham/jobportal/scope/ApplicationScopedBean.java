package com.shubham.jobportal.scope;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

@Getter
@Setter
@ApplicationScope
@Component
public class ApplicationScopedBean {
    public   int visitor_count;

    public ApplicationScopedBean() {
        System.out.println("Application Bean scoped bean is created");
    }
    public void incrementVisitor(){
        visitor_count++;
    }
}
