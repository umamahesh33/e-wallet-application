package com.example.ewalletapplication.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Wallet  {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    private String walletUserName;

    @Column(unique = true)
    private String userId;

    @Column(nullable = false)
    private String password;

//    private String authorities;

//    @Column(unique = true,nullable = false)
//    private String panId;

    @Enumerated(value = EnumType.STRING)
    private WalletStatus walletStatus;

    private long balance;

    @CreationTimestamp
    private Date createdOn;

    @UpdateTimestamp
    private Date updatedOn;

}
