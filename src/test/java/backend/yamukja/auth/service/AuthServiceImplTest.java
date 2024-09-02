package backend.yamukja.auth.service;

import backend.yamukja.auth.constant.Constant;
import backend.yamukja.auth.dto.AuthTokens;
import backend.yamukja.auth.dto.LoginRequest;
import backend.yamukja.auth.token.AccessToken;
import backend.yamukja.auth.token.RefreshToken;
import backend.yamukja.common.exception.RefreshTokenException;
import backend.yamukja.user.entity.User;
import backend.yamukja.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private TokenService tokenService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    private Key key;

    @BeforeEach
    void setUp() {
        String secret = "testsecrettestsecrettestsecrettestsecrettestsecret";
        key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    @Test
    @DisplayName("로그인: 성공 시나리오")
    public void login_WhenUserFound_ShouldReturnAuthTokens() {
        // Given
        LoginRequest loginRequest = new LoginRequest("username", "password");
        User user = User.builder().id(1L).userId("username").build();
        when(userRepository.findByUserId("username")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", user.getPassword())).thenReturn(true);

        AccessToken accessToken = new AccessToken(user.getId(), user.getUserId(), key);
        RefreshToken refreshToken = new RefreshToken();
        when(tokenService.generateAccessToken(user.getId(), user.getUserId())).thenReturn(accessToken);
        when(tokenService.generateRefreshToken(user.getId(), user.getUserId())).thenReturn(refreshToken);

        // When
        AuthTokens authTokens = authService.login(loginRequest);

        // Then
        assertNotNull(authTokens);
        assertEquals(accessToken.getToken(), authTokens.getLoginResponse().getAccessToken());
        assertEquals(refreshToken.getToken(), authTokens.getRefreshToken());
    }

    @Test
    @DisplayName("로그인 실패: 아이디 틀림")
    public void login_WhenInvalidPassword_ShouldThrowException() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("username", "password");
        User user = User.builder().id(1L).userId("username").build();
        when(userRepository.findByUserId("username")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", user.getPassword())).thenReturn(false);

        // When, Then
        BadCredentialsException e = assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
        assertEquals(Constant.WRONG_PASSWORD, e.getMessage());
    }

    @Test
    @DisplayName("로그인 실패: 아이디 틀림")
    public void login_WhenUsernameNotFound_ShouldThrowNoSuchElementException() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("username", "password");
        when(userRepository.findByUserId("username")).thenReturn(Optional.empty());

        // When, Then
        NoSuchElementException e = assertThrows(NoSuchElementException.class, () -> authService.login(loginRequest));
        assertEquals(Constant.WRONG_USERNAME, e.getMessage());
    }

    @Test
    @DisplayName("로그아웃: 성공 시나리오")
    public void logOut_WhenValidToken_ShouldCallDeleteRefreshToken() {
        // Given
        String refreshToken = "validRefreshToken";

        // When
        authService.logOut(refreshToken);

        // Then
        verify(tokenService, times(1)).deleteRefreshToken(refreshToken);
    }

    @Test
    @DisplayName("로그아웃: refresh token이 없으면 RefreshTokenException")
    public void logOut_WhenNoToken_ShouldThrowException() {
        // Given
        String refreshToken = "";

        // When, Then
        assertThrows(RefreshTokenException.class, () -> authService.logOut(refreshToken));
    }
}
