package com.example.ewalletapplication.service;

import com.example.ewalletapplication.helperClasses.AddTransactionRequest;
import com.example.ewalletapplication.helperClasses.TransactionRequest;
import com.example.ewalletapplication.model.AddedFrom;
import com.example.ewalletapplication.model.Transaction;
import com.example.ewalletapplication.model.TransactionStatus;
import com.example.ewalletapplication.model.TransactionType;
import com.example.ewalletapplication.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private static final String TRANSACTION_DETAILS="transaction_details";
    private static final String TRANSACTION_REPORT="transaction-report";
    private static final String ADD_TRANSACTION_DETAILS="add_transaction_details";
    private static final String ADD_TRANSACTION_REPORT="add_transaction-report";
    private static final String FROM_USER_NOTIFICATION_DETAILS="fromUser_notification_details";
    private static final String TO_USER_NOTIFICATION_DETAILS="toUser_notification_details";

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    KafkaTemplate<String ,String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RestTemplate restTemplate;

//  TODO:WE SHOULD GET ALL DETAILS FROM THE KAFKA MESSAGING
//    1 fromUser -> toUser :done
//    2 money that needs to be transferred :done
//    3 we need to check that the sending user must have enough money to do transaction :done
//    4 update balances of two users :done
//    5 update transaction status
//    6 publish details to notification-service to notify user


    public void sendMoney(TransactionRequest transactionRequest) {
        Transaction transaction = Transaction.builder()
                .fromUser(transactionRequest.getFromUser())
                .toUser(transactionRequest.getToUser())
                .Amount(transactionRequest.getAmount())
                .transactionId(UUID.randomUUID().toString())
                .transactionType(TransactionType.SEND)
                .transactionStatus(TransactionStatus.PENDING)
                .build();

        try {
            transaction = transactionRepository.save(transaction);

            JSONObject walletInformer = new JSONObject();
            walletInformer.put("fromUser", transaction.getFromUser());
            walletInformer.put("toUser", transaction.getToUser());
            walletInformer.put("amount", transaction.getAmount());
            walletInformer.put("transactionId", transaction.getTransactionId());
            kafkaTemplate.send(TRANSACTION_DETAILS, objectMapper.writeValueAsString(walletInformer));
//        TODO:tomorrow to start from wallet service ,code to listen tservice produced topic :done

        } catch (JsonProcessingException e) {
            e.getMessage();
        }
    }

    @KafkaListener(topics = {TRANSACTION_REPORT},groupId = "wallet_users")
    public void updateSendTransaction(String message) throws Exception{
        JSONObject response=objectMapper.readValue(message,JSONObject.class);
        TransactionStatus transactionStatus=TransactionStatus.valueOf((String) response.get("transactionStatus"));
        Transaction transaction=transactionRepository.findByTransactionId((String) response.get("transactionId"));
        transaction.setTransactionStatus(transactionStatus);
        try {
            transaction=transactionRepository.save(transaction);
        }catch (Exception e){
            throw new IllegalStateException(e.getMessage()+"can not update transaction!");
        }
//        TODO:SEND DETAILS THAT ARE REQUIRED TO NOTIFICATION SERVICE :DONE
        JSONObject notificationDetails=new JSONObject();
        if (transaction.getTransactionStatus()==TransactionStatus.SUCCESS) {
            notificationDetails.put("fromUser", transaction.getFromUser());
            notificationDetails.put("toUser", transaction.getToUser());
            notificationDetails.put("amount", transaction.getAmount());
            notificationDetails.put("status", transactionStatus.name());
            notificationDetails.put("transactionId",transaction.getTransactionId());
        }else {
            notificationDetails.put("fromUser", transaction.getFromUser());
            notificationDetails.put("toUser", null);
            notificationDetails.put("amount", transaction.getAmount());
            notificationDetails.put("status", transactionStatus.name());
            notificationDetails.put("transactionId",transaction.getTransactionId());
        }
        sendDetailsToNotificationService(notificationDetails);

    }

    public void addMoney(AddTransactionRequest addTransactionRequest) throws JsonProcessingException {
        Transaction transaction= Transaction.builder()
                .addedFrom(AddedFrom.valueOf(addTransactionRequest.getAddedFrom()))
                .toUser(addTransactionRequest.getToUser())
                .Amount(addTransactionRequest.getAmount())
                .transactionStatus(TransactionStatus.PENDING)
                .transactionType(TransactionType.ADD)
                .transactionId(UUID.randomUUID().toString())
                .build();

        try{
            transaction=transactionRepository.save(transaction);
            JSONObject addTransactionDetails=new JSONObject();
            addTransactionDetails.put("toUser",transaction.getToUser());
            addTransactionDetails.put("amount",transaction.getAmount());
            addTransactionDetails.put("transactionId",transaction.getTransactionId());
            kafkaTemplate.send(ADD_TRANSACTION_DETAILS,objectMapper.writeValueAsString(addTransactionDetails));
        }catch (Exception e){
            throw new IllegalStateException(e.getMessage()+" can not do the transaction!");
        }
    }

    @KafkaListener(topics = {ADD_TRANSACTION_REPORT},groupId = "wallet_users")
    public void updateAddMoneyTransaction(String message) throws JsonProcessingException {
        JSONObject response=objectMapper.readValue(message,JSONObject.class);
        Transaction transaction=transactionRepository.findByTransactionId((String) response.get("transactionId"));
        TransactionStatus transactionStatus=TransactionStatus.valueOf((String) response.get("status"));
        transaction.setTransactionStatus(transactionStatus);

        try{
            transaction=transactionRepository.save(transaction);
        }catch (Exception e){
            throw new IllegalStateException(e.getMessage()+" can not update addMoney transaction");
        }
        JSONObject notificationDetails=new JSONObject();
        notificationDetails.put("toUser",transaction.getToUser());
        notificationDetails.put("fromUser",transaction.getFromUser());
        notificationDetails.put("amount",transaction.getAmount());
        notificationDetails.put("status",transactionStatus.name());
        notificationDetails.put("transactionId",transaction.getTransactionId());
        sendDetailsToNotificationService(notificationDetails);
    }

    private void sendDetailsToNotificationService(JSONObject jsonObject) throws JsonProcessingException {
//        TODO:GET USERS MAIL FROM USER SERVICE AND RETURN IT TO NOTIFICATION SERVICE

        String fromUser = (String) jsonObject.get("fromUser");
        String toUser = (String) jsonObject.get("toUser");
        long amount=(long) jsonObject.get("amount");
        String status=(String) jsonObject.get("status");
        String transId=(String) jsonObject.get("transactionId");

        if (fromUser != null) {
            URI uri = URI.create("http://localhost:8000/user/email?userId=" + fromUser);
//            String fromUserEmail=restTemplate.exchange(uri, HttpMethod.GET, HttpEntity.EMPTY,String.class).getBody();
            String fromUserEmail=restTemplate.getForObject(uri,String.class);
            JSONObject fromUserDetails=new JSONObject();
            fromUserDetails.put("userId",fromUser);
            fromUserDetails.put("email",fromUserEmail);
            fromUserDetails.put("amount",amount);
            fromUserDetails.put("status",status);
            fromUserDetails.put("transactionId",transId);
            kafkaTemplate.send(FROM_USER_NOTIFICATION_DETAILS,objectMapper.writeValueAsString(fromUserDetails));
        }

        if(toUser!=null){
            URI uri=URI.create("http://localhost:8000/user/email?userId="+toUser);
            String toUserEmail=restTemplate.exchange(uri, HttpMethod.GET, HttpEntity.EMPTY,String.class).getBody();
            JSONObject toUserDetails=new JSONObject();
            toUserDetails.put("userId",toUser);
            toUserDetails.put("email",toUserEmail);
            toUserDetails.put("amount",amount);
            toUserDetails.put("status",status);
            toUserDetails.put("transactionId",transId);
            kafkaTemplate.send(TO_USER_NOTIFICATION_DETAILS,objectMapper.writeValueAsString(toUserDetails));
        }
    }

    public List<Transaction> getUserSendTransactions(String userId){
        return transactionRepository.findByFromUser(userId);
    }

    public List<Transaction> getUserAddTransactions(String userId){
        return transactionRepository.getAddTransactionsOfUser(userId);
    }
}
