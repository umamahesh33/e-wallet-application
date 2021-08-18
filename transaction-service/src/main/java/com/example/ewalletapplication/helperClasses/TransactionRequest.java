package com.example.ewalletapplication.helperClasses;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {

    private String fromUser;
    private String toUser;
    private long amount;

}
