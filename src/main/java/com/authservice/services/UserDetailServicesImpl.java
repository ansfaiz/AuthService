package com.authservice.services;

import com.authservice.entities.UserInfo;
import com.authservice.model.UserInfoDto;
import com.authservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Data
@Component
public class UserDetailServicesImpl implements UserDetailsService {
    private  final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public UserDetails loadUserByUsername (String byUsername) throws UsernameNotFoundException{
        UserInfo user=userRepository.findByUsername(byUsername);
        if(user==null){
            throw new UsernameNotFoundException("user not found ...!");
        }
        return new CustomUserDetails(user);
    }
    public UserInfo checkIfUserAlreadyExist(UserInfoDto userInfoDto){
        return userRepository.findByUsername(userInfoDto.getUsername());
    }
    public  boolean signUp(UserInfoDto userInfoDto){
        userInfoDto.setPassword(passwordEncoder.encode(userInfoDto.getPassword()));
        if(Objects.nonNull(checkIfUserAlreadyExist(userInfoDto))){
           return false;
        }
        String userId = UUID.randomUUID().toString();
        userRepository.save(new UserInfo().builder().userId(userId).username(userInfoDto.getUsername()).password(userInfoDto.getPassword()).roles(new HashSet<>()).build());
        return true;
    }
}
