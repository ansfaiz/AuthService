package com.authservice.repository;


import com.authservice.entities.UserInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository  extends CrudRepository<UserInfo, Long> {

    public UserInfo findByUsername(String username);

}
