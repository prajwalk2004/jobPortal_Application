package com.shubham.jobportal.scope;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scope")
@RequiredArgsConstructor
public class ScopeController {
    public final RequestScopeBean requestScopeBean;
     public final  SessionScopeBean sessionScopeBean;
     public final ApplicationScopedBean applicationScopedBean;
      @GetMapping("/request")
     public ResponseEntity<String> testRequest(){
         requestScopeBean.setName("Prajwal");
         return ResponseEntity.ok().body(requestScopeBean.getName());
     }
     @GetMapping("/session")
     public ResponseEntity<String>testSession(){
          sessionScopeBean.setName("Shubham");
          return  ResponseEntity.ok().body(sessionScopeBean.getName());
     }

    @GetMapping("/application")
    public ResponseEntity<Integer> testApplicationScope(){
          applicationScopedBean.incrementVisitor();
        return ResponseEntity.ok().body(applicationScopedBean.getVisitor_count());
    }
    @GetMapping("/test")
    public ResponseEntity<Integer>testScope(){
        return  ResponseEntity.ok().body(applicationScopedBean.getVisitor_count());
    }

}
