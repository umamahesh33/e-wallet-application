package com.example.ewalletapplication.helperClasses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddTransactionRequest {

    private String addedFrom;
    private long amount;

}