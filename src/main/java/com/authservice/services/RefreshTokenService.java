package com.authservice.services;

import com.authservice.entities.RefreshToken;
import com.authservice.entities.UserInfo;
import com.authservice.repository.RefreshTokenRepository;
import com.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;


    private static final long REFRESH_TOKEN_EXPIRATION_MS = 600000;
    @Transactional
    public RefreshToken createRefreshToken(String username) {

        UserInfo extractedUserInfo = userRepository.findByUsername(username);

        if (extractedUserInfo == null) {
            throw new RuntimeException("User not found: " + username);
        }
        refreshTokenRepository.deleteByUserInfo(extractedUserInfo);
        refreshTokenRepository.flush();
        String token;
        do{
            token=UUID.randomUUID().toString();
        }while (refreshTokenRepository.findByToken(token).isPresent());

        RefreshToken refreshToken = RefreshToken.builder()
                .userInfo(extractedUserInfo)
                .token(token)
                .expiry_date(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION_MS))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }
    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpiry_date().compareTo(Instant.now())<0){
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken()+" Refresh token is expired ,Please make a new login: ");
        }
        return token;
    }
    public Optional<RefreshToken> findByToken (String token){
        return refreshTokenRepository.findByToken(token);
    }


}