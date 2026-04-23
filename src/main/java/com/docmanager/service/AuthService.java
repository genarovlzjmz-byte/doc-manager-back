package com.docmanager.service;

import com.docmanager.dto.request.LoginRequest;
import com.docmanager.dto.request.RefreshTokenRequest;
import com.docmanager.dto.response.AuthResponse;
import com.docmanager.entity.RefreshToken;
import com.docmanager.entity.User;
import com.docmanager.mapper.UserMapper;
import com.docmanager.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String accessToken = tokenProvider.generateAccessToken(authentication);
        User user = (User) authentication.getPrincipal();
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

        log.info("Login exitoso para usuario: {}", user.getUsername());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .user(userMapper.toResponse(user))
                .build();
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
        String accessToken = tokenProvider.generateAccessToken(refreshToken.getUser().getUsername());

        log.info("Token renovado para usuario: {}", refreshToken.getUser().getUsername());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .user(userMapper.toResponse(refreshToken.getUser()))
                .build();
    }

    public void logout(User user) {
        refreshTokenService.deleteByUser(user);
        log.info("Logout exitoso para usuario: {}", user.getUsername());
    }
}
