package com.bundleebackend.bundleebackend.controller;

import com.bundleebackend.bundleebackend.records.LoginBody;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @PostMapping("/")
    public String login(@RequestBody LoginBody loginBody) {
        return "here";
    }
}
