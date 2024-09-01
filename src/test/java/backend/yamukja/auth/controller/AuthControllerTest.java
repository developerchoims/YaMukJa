package backend.yamukja.auth.controller;

import backend.yamukja.auth.constant.Constant;
import backend.yamukja.auth.dto.AuthTokens;
import backend.yamukja.auth.dto.LoginRequest;
import backend.yamukja.auth.service.AuthService;
import backend.yamukja.auth.service.TokenService;
import backend.yamukja.common.config.SecurityConfig;
import backend.yamukja.common.exception.RefreshTokenException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static backend.yamukja.auth.constant.Constant.REFRESH_TOKEN_NOT_FOUND;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@Import(SecurityConfig.class)
@ComponentScan(basePackages = {"backend.yamukja.auth.filter"})
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private TokenService tokenService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("로그인 테스트: 성공 시나리오")
    public void login_WhenValid_ShouldReturnAuthTokens() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("username", "password");
        AuthTokens authTokens = new AuthTokens("refreshToken", "accessToken", "username");

        when(authService.login(any(LoginRequest.class))).thenReturn(authTokens);

        // When, Then
        mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("REFRESH_TOKEN=refreshToken;")));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidLoginRequest")
    @DisplayName("로그인 실패")
    public void login_WhenInvalidRequestData_ShouldThrowException(RuntimeException exception, ResultMatcher status, String message) throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("username", "password");
        when(authService.login(any(LoginRequest.class))).thenThrow(exception);

        // When, Then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status)
                .andExpect(jsonPath("$.message").value(message));
    }

    private static Stream<Arguments> provideInvalidLoginRequest() {
        return Stream.of(
                Arguments.of(
                        new UsernameNotFoundException(Constant.WRONG_USERNAME),
                        status().isNotFound(),
                        Constant.WRONG_USERNAME
                ),
                Arguments.of(
                        new BadCredentialsException(Constant.WRONG_PASSWORD),
                        status().isUnauthorized(),
                        Constant.WRONG_PASSWORD
                )
        );
    }

    @Test
    @DisplayName("Access Token 재발급: 성공 시나리오")
    public void reissueToken_WhenValid_ShouldReturnNewAccessToken() throws Exception {
        // Given
        String refreshToken = "refreshToken";
        String newAccessToken = "newAccessToken";

        when(authService.reissueToken(refreshToken)).thenReturn(newAccessToken);

        // When, Then
        mockMvc.perform(post("/api/token/reissue")
                        .cookie(new Cookie("REFRESH_TOKEN", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(newAccessToken));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidReissueRequest")
    @DisplayName("Access Token 재발급: 실패")
    public void reissueToken_WhenInValid_ShouldThrowException(RuntimeException exception, ResultMatcher status, String message) throws Exception {
        // Given
        when(authService.reissueToken(any(String.class))).thenThrow(exception);

        // When, Then
        mockMvc.perform(post("/api/token/reissue")
                        .cookie(new Cookie("REFRESH_TOKEN", "refreshToken")))
                .andExpect(status)
                .andExpect(jsonPath("$.message").value(message));
    }

    private static Stream<Arguments> provideInvalidReissueRequest() {
        return Stream.of(
                Arguments.of(
                        new NoSuchElementException(REFRESH_TOKEN_NOT_FOUND),
                        status().isNotFound(),
                        Constant.REFRESH_TOKEN_NOT_FOUND
                ),
                Arguments.of(
                        new RefreshTokenException(Constant.REFRESH_TOKEN_EXPIRED),
                        status().isUnauthorized(),
                        Constant.REFRESH_TOKEN_EXPIRED
                )
        );
    }

    @Test
    @DisplayName("로그아웃")
    public void logOut() throws Exception {
        // Given
        String refreshToken = "refreshToken";

        // When, Then
        mockMvc.perform(post("/api/logout")
                        .cookie(new Cookie("REFRESH_TOKEN", refreshToken)))
                .andExpect(status().isNoContent())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("REFRESH_TOKEN=;")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Max-Age=0;")));

        verify(authService).logOut(refreshToken);
    }
}
