package com.shubham.jobportal.Company.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class controllerdemo {
    @GetMapping("/home")
    public String returnstring(){
        return "hello this is prajwal";
    }

}
//