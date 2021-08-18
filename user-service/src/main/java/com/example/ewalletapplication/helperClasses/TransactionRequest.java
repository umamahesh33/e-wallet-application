package com.example.ewalletapplication.helperClasses;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {

    private String toUser;
    private long amount;

}