package backend.yamukja.auth.service;

import backend.yamukja.auth.model.ActiveUser;
import backend.yamukja.auth.model.UserCustom;
import backend.yamukja.auth.repository.ActiveUserRepository;
import backend.yamukja.auth.token.AccessToken;
import backend.yamukja.auth.token.RefreshToken;
import backend.yamukja.common.exception.RefreshTokenException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.NoSuchElementException;

import static backend.yamukja.auth.constant.Constant.REFRESH_TOKEN_EXPIRED;
import static backend.yamukja.auth.constant.Constant.REFRESH_TOKEN_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final ActiveUserRepository activeUserRepository;
    @Value("${secret}")
    private String secret;
    private Key key;

    @PostConstruct
    public void init() {
        this.key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    @Override
    public AccessToken convertAccessToken(String token) {
        return new AccessToken(token, key);
    }

    @Override
    public void setAuthentication(Long userId, String username) {
        UserCustom userCustom = new UserCustom(userId, username);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userCustom, null, Collections.singleton(new SimpleGrantedAuthority("USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Override
    public AccessToken generateAccessToken(Long userId, String username) {
        return new AccessToken(userId, username, key);
    }

    @Override
    public RefreshToken generateRefreshToken(Long userId, String username) {
        RefreshToken refreshToken = new RefreshToken();

        // Refresh token Redis에 저장
        ActiveUser activeUser = new ActiveUser(userId, username, refreshToken);
        activeUserRepository.save(activeUser);

        return refreshToken;
    }

    public AccessToken refreshAccessToken(String refreshToken) {
        ActiveUser activeUser = activeUserRepository.findById(refreshToken)
                .orElseThrow(() -> new NoSuchElementException(REFRESH_TOKEN_NOT_FOUND));

        if (activeUser.getExpiredAt().isBefore(LocalDateTime.now())) {
            // refresh token 만료
            activeUserRepository.deleteById(refreshToken); // Redis에서 삭제
            throw new RefreshTokenException(REFRESH_TOKEN_EXPIRED);
        }

        return generateAccessToken(activeUser.getId(), activeUser.getUsername());
    }

    public void deleteRefreshToken(String refreshToken) {
        activeUserRepository.deleteById(refreshToken);
    }
}