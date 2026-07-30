package com.shubham.jobportal.scope;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.annotation.SessionScope;

@Getter
@Setter
@Component
@SessionScope
public class SessionScopeBean {
    String name;
    public SessionScopeBean() {
        System.out.println("Session ScopedBean scoped bean created");
    }
}
