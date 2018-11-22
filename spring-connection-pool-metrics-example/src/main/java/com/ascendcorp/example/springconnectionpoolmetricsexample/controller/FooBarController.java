package com.ascendcorp.example.springconnectionpoolmetricsexample.controller;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class FooBarController {

    RestTemplate restTemplate;

    public FooBarController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/foo")
    public String foo() {
        return restTemplate.getForObject("http://localhost:8080/foo-bar", String.class);
    }

    @GetMapping("/foo-bar")
    public String fooBar() {
        return "foo bar";
    }
}
