package backend.yamukja.auth.service;

import static org.junit.jupiter.api.Assertions.*;

import backend.yamukja.auth.constant.Constant;
import backend.yamukja.auth.model.ActiveUser;
import backend.yamukja.auth.model.UserCustom;
import backend.yamukja.auth.repository.ActiveUserRepository;
import backend.yamukja.auth.token.AccessToken;
import backend.yamukja.auth.token.RefreshToken;
import backend.yamukja.common.exception.RefreshTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceImplTest {
    @Mock
    private ActiveUserRepository activeUserRepository;

    @InjectMocks
    private TokenServiceImpl tokenService;

    private Key key;

    @BeforeEach
    void setUp() {
        String secret = "testsecrettestsecrettestsecrettestsecrettestsecret";

        // secret 필드 mocking
        ReflectionTestUtils.setField(tokenService, "secret", secret);

        // PostConstruct 호출
        tokenService.init();

        this.key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    @Test
    @DisplayName("JWT Token -> Access Token 객체")
    void convertAccessToken() {
        // given
        String username = "testUser";
        AccessToken testToken = new AccessToken(1L, username, key);

        // when
        AccessToken token = tokenService.convertAccessToken(testToken.getToken());

        // then
        assertNotNull(token);
        assertEquals(testToken.getToken(), token.getToken());
        assertEquals(username, token.getData().getSubject());
    }

    @Test
    @DisplayName("Authentication 세팅")
    void setAuthentication() {
        // given
        Long userId = 1L;
        String username = "testUser";

        // when
        tokenService.setAuthentication(userId, username);

        // then
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserCustom userCustom = (UserCustom) auth.getPrincipal();

        assertNotNull(auth);
        assertEquals(userId, userCustom.getId());
        assertEquals(username, userCustom.getUsername());
        assertTrue(auth.getAuthorities().contains(new SimpleGrantedAuthority("USER")));
    }

    @Test
    @DisplayName("Refresh Token 생성 및 저장")
    void generateRefreshToken() {
        Long userId = 1L;
        String username = "testUser";
        RefreshToken refreshToken = new RefreshToken();

        when(activeUserRepository.save(any(ActiveUser.class))).thenReturn(new ActiveUser(userId, username, refreshToken));

        RefreshToken result = tokenService.generateRefreshToken(userId, username);

        assertNotNull(result);
        verify(activeUserRepository).save(any(ActiveUser.class));
    }

    @Test
    @DisplayName("Access Token 재발급: 성공 시나리오")
    void refreshAccessToken_WhenTokenIsValid_ShouldReturnNewAccessToken() {
        // Given
        String refreshTokenString = "validRefreshToken";
        Long userId = 1L;
        String username = "testUser";
        RefreshToken refreshToken = new RefreshToken();

        ActiveUser activeUser = new ActiveUser(userId, username, refreshToken);

        when(activeUserRepository.findById(refreshTokenString)).thenReturn(Optional.of(activeUser));

        // When
        AccessToken accessToken = tokenService.refreshAccessToken(refreshTokenString);

        assertNotNull(accessToken);
        assertEquals(username, accessToken.getData().getSubject());
        assertTrue(accessToken.getExpiredAt().isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("Redis에 해당 Refresh Token이 존재하지 않으면 NoSuchElementException")
    void refreshAccessToken_WhenRefreshTokenIsNotInRedis_ShouldThrowException() {
        // Given
        when(activeUserRepository.findById(anyString())).thenReturn(Optional.empty());

        // When, then
        NoSuchElementException e = assertThrows(NoSuchElementException.class, () ->
                tokenService.refreshAccessToken("expired refresh token")
        );

        assertEquals(Constant.REFRESH_TOKEN_NOT_FOUND, e.getMessage());
    }

    @Test
    @DisplayName("Refresh Token 만료 시 RefreshTokenException")
    void refreshAccessToken_WhenTokenIsExpired_ShouldThrowRefreshTokenException() {
        String refreshTokenString = "expiredRefreshToken";
        LocalDateTime expiredDate = LocalDateTime.now().minusDays(1); // 이미 만료된 토큰

        ActiveUser activeUser = mock(ActiveUser.class);
        when(activeUser.getExpiredAt()).thenReturn(expiredDate);

        when(activeUserRepository.findById(refreshTokenString)).thenReturn(Optional.of(activeUser));

        RefreshTokenException e = assertThrows(RefreshTokenException.class, () ->
                tokenService.refreshAccessToken(refreshTokenString)
        );

        assertEquals(Constant.REFRESH_TOKEN_EXPIRED, e.getMessage());
    }

    @Test
    @DisplayName("리프레쉬 토큰 삭제")
    void testDeleteRefreshToken() {
        String refreshTokenString = "tokenToDelete";

        tokenService.deleteRefreshToken(refreshTokenString);

        verify(activeUserRepository).deleteById(refreshTokenString);
    }
}
