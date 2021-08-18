package com.example.ewalletapplication.helperClasses;

import lombok.*;
import org.springframework.security.config.web.servlet.SecurityMarker;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequest {
    private String name;
    private String mobileNumber;
    private String email;
    private int age;
}
