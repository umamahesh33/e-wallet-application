package com.example.ewalletapplication.repository;

import com.example.ewalletapplication.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<UserModel,Integer> {

    UserModel findByUserId(String userId);

    @Transactional
    @Modifying
    @Query("UPDATE UserModel a " +
            "SET a.enabled = TRUE WHERE a.id = ?1")
    void enableUser(int id);

}
