package com.example.ewalletapplication.helperClasses;


import lombok.*;
import org.springframework.stereotype.Service;

@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BalanceResponse {

    private String userId;
    private String userName;
    private String  balance;

}
