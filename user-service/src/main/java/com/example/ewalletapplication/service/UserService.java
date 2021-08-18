package com.example.ewalletapplication.service;

import com.example.ewalletapplication.helperClasses.AddTransactionRequest;
import com.example.ewalletapplication.helperClasses.TransactionRequest;
import com.example.ewalletapplication.helperClasses.UserRequest;
import com.example.ewalletapplication.helperClasses.UserUpdateRequest;
import com.example.ewalletapplication.model.ConfirmationStatus;
import com.example.ewalletapplication.model.ConfirmationToken;
import com.example.ewalletapplication.model.UserModel;
import com.example.ewalletapplication.repository.ConfirmationTokenRepository;
import com.example.ewalletapplication.repository.UserCacheRepository;
import com.example.ewalletapplication.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService implements UserDetailsService {

    private static final String WALLET_CREATE_TOPIC="create_user_wallet";
    private static final String MAIL_VERIFICATION_TOPIC="verify_mail";

    @Autowired
    KafkaTemplate<String ,String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserCacheRepository userCacheRepository;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    SimpleMailMessage simpleMailMessage;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    RestTemplate restTemplate;


    public String createUser(UserRequest userRequest) throws Exception {


        UserModel userModel = UserModel.builder()
                .userId(userRequest.getUserId())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .authorities("role_user")
                .email(userRequest.getEmail())
                .name(userRequest.getName())
                .age(userRequest.getAge())
                .mobileNumber(userRequest.getMobileNumber())
                .build();

        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .confirmationStatus(ConfirmationStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .token(UUID.randomUUID().toString())
                .build();


        try{
            userModel =userRepository.save(userModel);
            confirmationToken.setUserModel(userModel);
            confirmationToken= confirmationTokenRepository.save(confirmationToken);
            sendDetailsToNotificationService(confirmationToken);

            JSONObject jsonObject=new JSONObject();
            jsonObject.put("userName",userModel.getName());
            jsonObject.put("userId",userModel.getUserId());
            jsonObject.put("password",userModel.getPassword());
            jsonObject.put("authorities",userModel.getAuthorities());

            kafkaTemplate.send(WALLET_CREATE_TOPIC,objectMapper.writeValueAsString(jsonObject));

            return "please verify your email!";
        }catch (Exception e){
            throw new Exception(e.getMessage()+ " :cannot create user, Please try again! ");
        }
    }

    public String updateUser(UserUpdateRequest userUpdateRequest) throws Exception {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        UserModel userModel=(UserModel) authentication.getPrincipal();
        if(userUpdateRequest.getName()!=null){userModel.setName(userUpdateRequest.getName());}
        if(userUpdateRequest.getAge()!=0){userModel.setAge(userUpdateRequest.getAge());}
        if(userUpdateRequest.getEmail()!=null){userModel.setEmail(userUpdateRequest.getEmail());}
        if(userUpdateRequest.getMobileNumber()!=null){userModel.setMobileNumber(userUpdateRequest.getMobileNumber());}

        try{
            userRepository.save(userModel);
            return "User Updated Successfully!";
        }catch(Exception e){
            throw new Exception("User not updated! Please try again.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        UserModel userModel=userCacheRepository.getUserFromCache(userId);

        if(userModel==null){
            userModel=userRepository.findByUserId(userId);
            userCacheRepository.setUserInCache(userModel,userId);
        }
        return userModel;
    }

    public void sendDetailsToNotificationService(ConfirmationToken confirmationToken) throws Exception {

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("toEmail",confirmationToken.getUserModel().getEmail());
        jsonObject.put("message","verify your mail by clicking that hyperlink\n"+"http://localhost:8000/user/validate?token="+confirmationToken.getToken());

       kafkaTemplate.send(MAIL_VERIFICATION_TOPIC,objectMapper.writeValueAsString(jsonObject));

    }

    @Transactional
    public String verifyMailId(String token){
        ConfirmationToken confirmationToken=null;
        confirmationToken=confirmationTokenRepository.findByToken(token);

        if(confirmationToken==null){
            throw new IllegalStateException("token not found");
        }

        if(confirmationToken.getConfirmedAt()!=null){
            throw new IllegalStateException("emailId already verified");
        }

        if(confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())){
            confirmationToken.setConfirmationStatus(ConfirmationStatus.EXPIRED);
            confirmationTokenRepository.save(confirmationToken);
            return "token expired";
        }

        if(confirmationToken!=null){

            userRepository.enableUser(confirmationToken.getUserModel().getId());
            confirmationToken.setConfirmedAt(LocalDateTime.now());
            confirmationToken.setConfirmationStatus(ConfirmationStatus.SUCCESS);
            confirmationTokenRepository.save(confirmationToken);

            return "Email verified successfully! now you can login with your credentials";
        }

        return "unexpected state";
    }

    public String getUser(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        UserModel userModel=(UserModel) authentication.getPrincipal();
        return "hi! "+userModel.getName();
    }

    public Map<String,String> getUserBalance(){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        UserModel userModel=(UserModel) authentication.getPrincipal();
        URI uri=URI.create("http://localhost:9000/wallet/balance?id="+userModel.getUserId());
        JSONObject jsonObject=restTemplate.exchange(uri, HttpMethod.GET, HttpEntity.EMPTY,JSONObject.class).getBody();
        Map<String ,String> resultMap=new HashMap<>();
        resultMap.put("balance",(String) jsonObject.get("balance"));
        resultMap.put("name",(String) jsonObject.get("userName"));
        return resultMap;
    }

    public String getUserMail(String userId){
        return userRepository.findByUserId(userId).getEmail();
    }

    public String sendMoney(TransactionRequest transactionRequest){

        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        UserModel userModel=(UserModel)authentication.getPrincipal();

        URI uri=URI.create("http://localhost:7000/transaction/send");
        JSONObject jsonObject=objectMapper.convertValue(transactionRequest,JSONObject.class);
        jsonObject.put("fromUser",userModel.getUserId());

        HttpHeaders headers=new HttpHeaders();
        HttpEntity<JSONObject> entity=new HttpEntity<>(jsonObject,headers);
        return restTemplate.exchange(uri,HttpMethod.POST,entity,String.class).getBody();
    }

    public String addMoney(AddTransactionRequest addTransactionRequest){

        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        UserModel userModel=(UserModel)authentication.getPrincipal();

        URI uri=URI.create("http://localhost:7000/transaction/add");
        JSONObject jsonObject=objectMapper.convertValue(addTransactionRequest,JSONObject.class);
        jsonObject.put("toUser",userModel.getUserId());

        HttpHeaders headers=new HttpHeaders();
        HttpEntity<JSONObject> entity=new HttpEntity<>(jsonObject,headers);
        return restTemplate.exchange(uri,HttpMethod.POST,entity,String.class).getBody();
    }

    public JSONArray getUserSendTransactions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserModel userModel = (UserModel) authentication.getPrincipal();

        URI uri = URI.create("http://localhost:7000/transaction/send/statement?userId=" + userModel.getUserId());
        return restTemplate.exchange(uri, HttpMethod.GET, HttpEntity.EMPTY, JSONArray.class).getBody();
    }

    public JSONArray getUserAddTransactions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserModel userModel = (UserModel) authentication.getPrincipal();

        URI uri = URI.create("http://localhost:7000/transaction/add/statement?userId=" + userModel.getUserId());
        return restTemplate.exchange(uri, HttpMethod.GET, HttpEntity.EMPTY, JSONArray.class).getBody();
    }


}
