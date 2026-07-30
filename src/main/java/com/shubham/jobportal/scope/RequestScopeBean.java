package com.shubham.jobportal.scope;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Getter
@Setter
@Component
@RequestScope
public class RequestScopeBean {
    String name;
    public RequestScopeBean() {
        System.out.println("Request scoped bean created");
    }
}
