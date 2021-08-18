package com.example.ewalletapplication.controller;

import com.example.ewalletapplication.helperClasses.AddTransactionRequest;
import com.example.ewalletapplication.helperClasses.TransactionRequest;
import com.example.ewalletapplication.model.Transaction;
import com.example.ewalletapplication.service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/send")
    public void sendMoney(@RequestBody TransactionRequest transactionRequest){
            transactionService.sendMoney(transactionRequest);
    }

    @PostMapping("/add")
    public void addMoney(@RequestBody AddTransactionRequest addTransactionRequest) throws JsonProcessingException {
        transactionService.addMoney(addTransactionRequest);

    }

    @GetMapping("/send/statement")
    public List<Transaction> getUserSendTransactions(@RequestParam("userId")String userID){
        return transactionService.getUserSendTransactions(userID);
    }

    @GetMapping("/add/statement")
    public List<Transaction> getUserAddTransactions(@RequestParam("userId")String userId){
        return transactionService.getUserAddTransactions(userId);
    }


}
