package com.example.ewalletapplication.service;

import com.example.ewalletapplication.helperClasses.BalanceResponse;
import com.example.ewalletapplication.model.Wallet;
import com.example.ewalletapplication.model.WalletStatus;
import com.example.ewalletapplication.repository.WalletRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class WalletService {

    private static final String WALLET_CREATE_TOPIC="create_user_wallet";
    private static final String TRANSACTION_DETAILS="transaction_details";
    private static final String TRANSACTION_REPORT="transaction-report";
    private static final String ADD_TRANSACTION_DETAILS="add_transaction_details";
    private static final String ADD_TRANSACTION_REPORT="add_transaction-report";

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    KafkaTemplate<String ,String> kafkaTemplate;


    @Autowired
    ObjectMapper objectMapper;

    @KafkaListener(topics = {WALLET_CREATE_TOPIC},groupId = "wallet_users")
    public void createWallet(String message) throws JsonProcessingException {
        JSONObject jsonObject=objectMapper.readValue(message,JSONObject.class);
        Wallet wallet= Wallet.builder()
                .walletUserName((String) jsonObject.get("userName"))
                .userId((String) jsonObject.get("userId"))
                .password((String) jsonObject.get("password"))
//                .authorities((String) jsonObject.get("authorities"))
                .balance(50)
                .walletStatus(WalletStatus.NOT_ACTIVATED)
                .build();

        try{
            wallet.setWalletStatus(WalletStatus.ACTIVATED);
            walletRepository.save(wallet);
        }catch (Exception e){
            throw new IllegalStateException("wallet not created");
        }
    }

    @KafkaListener(topics = {TRANSACTION_DETAILS},groupId = "wallet_users")
    public void sendMoney(String message) throws Exception {
        JSONObject transactionDetail=objectMapper.readValue(message,JSONObject.class);
        Wallet fromUser=walletRepository.findByUserId((String) transactionDetail.get("fromUser"));
        Wallet toUser=walletRepository.findByUserId((String) transactionDetail.get("toUser"));
        int amount=(int) transactionDetail.get("amount");
        String transactionId=(String) transactionDetail.get("transactionId");

        try {
            JSONObject transactionReport = new JSONObject();
            if (fromUser.getBalance() < amount || toUser == null) {
                transactionReport.put("transactionStatus", "FAILED");
                transactionReport.put("transactionId", transactionId);
                transactionReport.put("remarks", "from user wallet balance is not enough!");
            } else {
                fromUser.setBalance(fromUser.getBalance() - amount);
                toUser.setBalance(toUser.getBalance() + amount);

                fromUser = walletRepository.save(fromUser);
                toUser = walletRepository.save(toUser);
                transactionReport.put("transactionStatus","SUCCESS");
                transactionReport.put("transactionId",transactionId);
            }
            kafkaTemplate.send(TRANSACTION_REPORT, objectMapper.writeValueAsString(transactionReport));
        }catch (Exception e){
            throw new Exception(e.getMessage()+"Transaction can not be done!");
        }
    }

    @KafkaListener(topics = {ADD_TRANSACTION_DETAILS},groupId = "wallet_users")
    public void addMoneyToWallet(String message) throws Exception {
        JSONObject addTransactionDetails=objectMapper.readValue(message,JSONObject.class);
        Wallet toUser=walletRepository.findByUserId((String) addTransactionDetails.get("toUser"));
        int amount=(int) addTransactionDetails.get("amount");
        String transactionId=(String) addTransactionDetails.get("transactionId");

        JSONObject addTransactionReport=new JSONObject();
        try{
            toUser.setBalance(toUser.getBalance()+amount);
            walletRepository.save(toUser);
            addTransactionReport.put("transactionId",transactionId);
            addTransactionReport.put("status","SUCCESS");
            kafkaTemplate.send(ADD_TRANSACTION_REPORT,objectMapper.writeValueAsString(addTransactionReport));
        }catch (Exception e){
            throw new Exception(e.getMessage()+"Illegal stage occurred transaction was pending");
        }

    }


    public BalanceResponse getUserBalance(String userId){
        Wallet wallet=walletRepository.findByUserId(userId);

        if(wallet!=null) {
            return BalanceResponse.builder().userName(wallet.getWalletUserName())
                    .balance(wallet.getBalance()+"")
                    .userId(wallet.getUserId())
                    .build();
        }
        else {
            throw new IllegalStateException("User Wallet Not Found!");
        }
    }
}
