package com.epp.demoapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {
    @GetMapping("")
    public String printHello(String name) {
        return "hello " + name;
    }

    @GetMapping("/nametest")
    public String printName(String name) {
        return "Hi " + name;
    }
}