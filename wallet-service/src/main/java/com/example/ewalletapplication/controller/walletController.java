package com.example.ewalletapplication.controller;

import com.example.ewalletapplication.helperClasses.BalanceResponse;
import com.example.ewalletapplication.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
public class walletController {

    @Autowired
    WalletService walletService;

    @GetMapping("/balance")
    public BalanceResponse getBalance(@RequestParam("id")String userId){return walletService.getUserBalance(userId);}

}
