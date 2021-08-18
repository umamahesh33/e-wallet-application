package com.example.ewalletapplication.repository;

import com.example.ewalletapplication.model.UserModel;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class UserCacheRepository {

    private static final String REDIS_USER_PREFIX="user::";

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    public UserModel getUserFromCache(String userId){
        UserModel userModel=(UserModel) redisTemplate.opsForValue().get(REDIS_USER_PREFIX+userId);
        return userModel;
    }

    public void setUserInCache(UserModel userModel,String userId){
        redisTemplate.opsForValue().set(userId,userModel, Duration.ofMinutes(30));
    }
}
