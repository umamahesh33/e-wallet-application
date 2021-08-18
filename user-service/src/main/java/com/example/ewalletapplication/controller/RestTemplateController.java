package com.example.ewalletapplication.controller;

import com.example.ewalletapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestTemplateController {

    @Autowired
    UserService userService;

    @GetMapping("/user/email")
    public String userMailByUserId(@RequestParam("userId") String userId){
        return userService.getUserMail(userId);
    }
}
