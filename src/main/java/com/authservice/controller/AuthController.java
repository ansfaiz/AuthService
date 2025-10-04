package com.authservice.controller;


import com.authservice.entities.RefreshToken;
import com.authservice.model.UserInfoDto;
import com.authservice.request.AuthRequestDto;
import com.authservice.request.RefreshTokenRequestDto;
import com.authservice.responses.JwtResponseDto;
import com.authservice.services.JwtService;
import com.authservice.services.RefreshTokenService;
import com.authservice.services.UserDetailServicesImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller handling user authentication and registration endpoints.
 * Base path: /auth/v1
 */
@RestController
@RequestMapping("/auth/v1")
public class AuthController {

    @Autowired
    private UserDetailServicesImpl userDetailsService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Endpoint to register a new user and immediately issue an Access Token and Refresh Token (Auto-Login).
     * @param userInfoDto The user data transfer object containing sign-up details.
     * @return ResponseEntity containing JWT and Refresh Token on success, or conflict response.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody UserInfoDto userInfoDto) {
        // The service handles password encoding and user existence check.
        boolean success = userDetailsService.signUp(userInfoDto);

        if (success) {
            // 1. Load UserDetails (necessary for token generation)
            UserDetails userDetails = userDetailsService.loadUserByUsername(userInfoDto.getUsername());

            // 2. Generate Access Token (JWT)
            String accessToken = jwtService.generateToken(userDetails);

            // 3. Create Refresh Token
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userInfoDto.getUsername());

            // 4. Return tokens using JwtResponseDto (acting as an immediate login response)
            return ResponseEntity.ok(
                    JwtResponseDto.builder()
                    .accessToken(accessToken)
                    .token(refreshToken.getToken()) // Mapped to 'token' field in DTO
                    .build()
            );
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists.");
        }
    }

    /**
     * Endpoint for user login, issuing a JWT and a Refresh Token upon successful authentication.
     * Uses AuthRequestDto for input and JwtResponseDto for output.
     * @param authRequestDto The authentication request containing username and password.
     * @return ResponseEntity containing JWT and Refresh Token.
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequestDto authRequestDto) {
        // 1. Authenticate user credentials using the AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequestDto.getUsername(), authRequestDto.getPassword())
        );

        if (authentication.isAuthenticated()) {
            // 2. Load UserDetails for token generation
            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequestDto.getUsername());

            // 3. Generate Access Token (JWT)
            String accessToken = jwtService.generateToken(userDetails);

            // 4. Create Refresh Token
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDto.getUsername());

            // 5. Return tokens using JwtResponseDto
            return ResponseEntity.ok(JwtResponseDto.builder()
                    .accessToken(accessToken)
                    .token(refreshToken.getToken())
                    .build());

        } else {
            // Handle cases where authentication fails (though AuthenticationManager usually throws an exception)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        }
    }

    /**
     * Endpoint to get a new Access Token using a valid Refresh Token.
     * Uses RefreshTokenRequestDto for input and JwtResponseDto for output.
     * @param request The request containing the expired Refresh Token string.
     * @return ResponseEntity containing a new JWT and the original Refresh Token.
     */
    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDto request) {
        // 1. Find the Refresh Token in the database
        Optional<RefreshToken> tokenOptional = refreshTokenService.findByToken(request.getToken());

        if (tokenOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Refresh Token is not found or is invalid.");
        }

        RefreshToken token = tokenOptional.get();

        try {
            // 2. Verify if the token has expired
            // If expired, the service method will delete the token and throw a RuntimeException.
            refreshTokenService.verifyExpiration(token);

            // 3. Load UserDetails from the Refresh Token's associated UserInfo
            UserDetails userDetails = userDetailsService.loadUserByUsername(token.getUserInfo().getUsername());

            // 4. Generate a new Access Token
            String newAccessToken = jwtService.generateToken(userDetails);

            // 5. Return the new Access Token along with the existing Refresh Token
            return ResponseEntity.ok(JwtResponseDto.builder()
                    .accessToken(newAccessToken)
                    .token(request.getToken())
                    .build());

        } catch (RuntimeException e) {
            System.err.println("Refresh token error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Refresh Token is expired. Please log in again.");
        }
    }
}
