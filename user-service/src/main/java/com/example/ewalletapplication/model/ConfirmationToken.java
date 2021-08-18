package com.example.ewalletapplication.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String token;

    @CreationTimestamp
    private Date createdOn;

    private LocalDateTime expiresAt;

    private LocalDateTime confirmedAt;

    @Enumerated(value = EnumType.STRING)
    private ConfirmationStatus confirmationStatus;

    @ManyToOne
    @JoinColumn
    private UserModel userModel;

}
