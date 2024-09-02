package backend.yamukja.auth.service;

import backend.yamukja.auth.dto.AuthTokens;
import backend.yamukja.auth.dto.LoginRequest;
import backend.yamukja.auth.token.AccessToken;
import backend.yamukja.auth.token.RefreshToken;
import backend.yamukja.common.exception.RefreshTokenException;
import backend.yamukja.user.entity.User;
import backend.yamukja.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.NoSuchElementException;

import static backend.yamukja.auth.constant.Constant.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final TokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public AuthTokens login(LoginRequest loginRequest) {
        User user = userRepository.findByUserId(loginRequest.getUsername())
                .orElseThrow(() -> new NoSuchElementException(WRONG_USERNAME));

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException(WRONG_PASSWORD);
        }

        // Security Context 저장
        tokenService.setAuthentication(user.getId(), user.getUserId());

        // Access Token, Refresh Token 생성
        AccessToken accessToken = tokenService.generateAccessToken(user.getId(), user.getUserId());
        RefreshToken refreshToken = tokenService.generateRefreshToken(user.getId(), user.getUserId());

        return new AuthTokens(refreshToken.getToken(), accessToken.getToken(), user.getUserId());
    }

    @Override
    @Transactional(readOnly = true)
    public String reissueToken(String refreshToken) {
        return tokenService.refreshAccessToken(refreshToken).getToken();
    }

    @Override
    @Transactional
    public void logOut(String refreshToken) {
        if(!StringUtils.hasText(refreshToken)) {
            throw new RefreshTokenException(NO_REFRESH_TOKEN);
        }

        tokenService.deleteRefreshToken(refreshToken);
    }
}
