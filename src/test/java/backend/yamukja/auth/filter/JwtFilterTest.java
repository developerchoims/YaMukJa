package backend.yamukja.auth.filter;

import static org.junit.jupiter.api.Assertions.*;

import backend.yamukja.auth.service.TokenService;
import backend.yamukja.auth.token.AccessToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtFilterTest {

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private JwtFilter jwtFilter;

    @Mock
    private FilterChain filterChain;

    @Test
    @DisplayName("유효한 액세스 토큰 도착 시, 인증 정보 설정")
    public void doFilterInternal_WithValidToken_ShouldSetAuthentication() throws ServletException, IOException {
        // Given
        String validToken = "ValidToken";
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader(JwtFilter.AUTHORIZATION_HEADER, JwtFilter.BEARER_PREFIX + validToken);

        Claims claims = mock(Claims.class);
        when(claims.get("aud")).thenReturn("1");
        when(claims.getSubject()).thenReturn("username");

        AccessToken accessToken = mock(AccessToken.class);
        when(accessToken.getData()).thenReturn(claims);

        when(tokenService.convertAccessToken(anyString())).thenReturn(accessToken);

        // When
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(tokenService, times(1)).convertAccessToken(validToken);
        verify(tokenService, times(1)).setAuthentication(1L, "username");
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("잘못된 토큰이 도착 시, 예외가 발생하고 인증 정보 설정 X")
    public void doFilterInternal_WithInvalidToken_ShouldNotSetAuthentication() throws ServletException, IOException {
        // Given
        String invalidToken = "InvalidToken";
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader(JwtFilter.AUTHORIZATION_HEADER, JwtFilter.BEARER_PREFIX + invalidToken);

        when(tokenService.convertAccessToken(anyString())).thenThrow(JwtException.class);

        // When
        assertThrows(JwtException.class, () -> jwtFilter.doFilterInternal(request, response, filterChain));

        // Then
        verify(tokenService, times(1)).convertAccessToken(invalidToken);
        verify(tokenService, never()).setAuthentication(anyLong(), anyString());
    }

    @Test
    @DisplayName("토큰이 없는 요청 도착 시, 인증 정보 설정 X")
    public void doFilterInternal_NoToken_ShouldNotSetAuthentication() throws ServletException, IOException {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // When
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(tokenService, never()).convertAccessToken(anyString());
        verify(tokenService, never()).setAuthentication(anyLong(), anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
