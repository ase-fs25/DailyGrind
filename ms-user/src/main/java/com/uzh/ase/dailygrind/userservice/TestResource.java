package com.uzh.ase.dailygrind.userservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestResource {

    @GetMapping("/test")
    public String test() {
        return "Hello World";
    }

}
