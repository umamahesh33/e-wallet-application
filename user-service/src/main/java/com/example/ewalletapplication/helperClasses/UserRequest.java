package com.example.ewalletapplication.helperClasses;


import lombok.*;

import javax.persistence.GeneratedValue;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {

    private String userId;
    private String password;
    private String name;
    private String mobileNumber;
    private String email;
    private int age;
}
