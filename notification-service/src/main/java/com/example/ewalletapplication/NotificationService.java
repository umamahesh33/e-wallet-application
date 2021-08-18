package com.example.ewalletapplication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final String FROM_USER_NOTIFICATION_DETAILS="fromUser_notification_details";
    private static final String TO_USER_NOTIFICATION_DETAILS="toUser_notification_details";
    private static final String MAIL_VERIFICATION_TOPIC="verify_mail";

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    SimpleMailMessage simpleMailMessage;

    @Autowired
    ObjectMapper objectMapper;


    @KafkaListener(topics = {MAIL_VERIFICATION_TOPIC},groupId = "notifications")
    public void mailConfirmation(String msg) throws JsonProcessingException {

        JSONObject response=objectMapper.readValue(msg,JSONObject.class);

        simpleMailMessage.setFrom("EWallet.noreply@gmail.com");
        simpleMailMessage.setTo((String) response.get("toEmail"));
        simpleMailMessage.setSubject("MAIL VERIFICATION");
        simpleMailMessage.setText((String) response.get("message"));
        javaMailSender.send(simpleMailMessage);
    }

    @KafkaListener(topics = {FROM_USER_NOTIFICATION_DETAILS},groupId = "notifications")
    public void notifyFromUser(String message) throws JsonProcessingException {
        JSONObject response=objectMapper.readValue(message,JSONObject.class);
        String fromUser=(String) response.get("userId");
        String fromUserMail=(String) response.get("email");
        String status=(String) response.get("status");
        String transId=(String) response.get("transactionId");
        int amount=(int) response.get("amount");
        String msgToSend="hi "+fromUser+" ! your transfer of amount "+amount+"RS is "+status+" with transactionID: "+transId;

        simpleMailMessage.setFrom("Ewallet.noreply@gmail.com");
        simpleMailMessage.setTo(fromUserMail);
        simpleMailMessage.setSubject("TRANSACTION DETAILS");
        simpleMailMessage.setText(msgToSend);
        javaMailSender.send(simpleMailMessage);

    }

    @KafkaListener(topics = {TO_USER_NOTIFICATION_DETAILS},groupId = "notifications")
    public void notifyToUser(String message) throws JsonProcessingException {
        JSONObject response=objectMapper.readValue(message,JSONObject.class);
        String toUser=(String) response.get("userId");
        String toUserMail=(String) response.get("email");
        String status=(String) response.get("status");
        String transId=(String) response.get("transactionId");
        int amount=(int) response.get("amount");
        String msgToSend="hi "+toUser+" ! your have received  "+amount+"RS, Status: "+status+" with transactionID: "+transId;

        simpleMailMessage.setFrom("Ewallet.noreply@gmail.com");
        simpleMailMessage.setTo(toUserMail);
        simpleMailMessage.setSubject("TRANSACTION DETAILS");
        simpleMailMessage.setText(msgToSend);
        javaMailSender.send(simpleMailMessage);

    }
}
