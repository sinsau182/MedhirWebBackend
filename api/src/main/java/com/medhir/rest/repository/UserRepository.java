package com.medhir.rest.repository;

import com.medhir.rest.model.UserModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserModel, String> {
    Optional<UserModel> findByemail(String email);
    Optional<UserModel> findByPhone(String phone);
}
