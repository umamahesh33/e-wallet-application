package com.example.ewalletapplication.helperClasses;

import com.example.ewalletapplication.model.AddedFrom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddTransactionRequest {

    private String toUser;
    private String addedFrom;
    private long amount;

}
