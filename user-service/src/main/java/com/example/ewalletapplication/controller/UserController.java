package com.example.ewalletapplication.controller;

import com.example.ewalletapplication.helperClasses.AddTransactionRequest;
import com.example.ewalletapplication.helperClasses.TransactionRequest;
import com.example.ewalletapplication.helperClasses.UserRequest;
import com.example.ewalletapplication.helperClasses.UserUpdateRequest;
import com.example.ewalletapplication.service.UserService;
import org.apache.coyote.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;


    @GetMapping("/home")
    public String getUser(){return userService.getUser();}

    @GetMapping("/wallet-balance")
    public Map<String ,String> getUserBalance(){
        return userService.getUserBalance();
    }

    @RequestMapping(value = "/validate",method = RequestMethod.GET)
    public String validateEmailOfUser(@RequestParam("token") String token){
        return userService.verifyMailId(token);
    }


    @PostMapping("/create")
    public String createUser(@RequestBody UserRequest userRequest) throws Exception {
        return userService.createUser(userRequest);
    }

    @PutMapping("/update")
    public String updateUser(@RequestBody UserUpdateRequest userUpdateRequest) throws Exception {
        return userService.updateUser(userUpdateRequest);
    }

    @PostMapping("/transaction/send")
    public void sendMoney(@RequestBody TransactionRequest transactionRequest){
        userService.sendMoney(transactionRequest);
    }

    @PostMapping("/transaction/add")
    public void addMoney(@RequestBody AddTransactionRequest addTransactionRequest){
        userService.addMoney(addTransactionRequest);
    }

    @GetMapping("transaction/send")
    public JSONArray getUserSendTransactions(){
        return userService.getUserSendTransactions();
    }

    @GetMapping("transaction/add")
    public JSONArray getUserAddTransactions(){
        return userService.getUserAddTransactions();
    }



}
