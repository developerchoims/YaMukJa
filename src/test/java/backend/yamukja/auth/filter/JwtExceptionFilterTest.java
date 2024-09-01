package backend.yamukja.auth.filter;

import backend.yamukja.common.dto.ErrorResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.PrintWriter;

import static backend.yamukja.auth.constant.Constant.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtExceptionFilterTest {

    @InjectMocks
    private JwtExceptionFilter jwtExceptionFilter;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private PrintWriter printWriter;

    @Test
    @DisplayName("ExpiredJwtException 발생 시, 적절한 예외처리 객체 반환")
    void doFilterInternal_WithExpiredJwtException() throws IOException, ServletException {
        // Given
        doThrow(new ExpiredJwtException(null, null, "Token expired"))
                .when(filterChain).doFilter(request, response);
        when(response.getWriter()).thenReturn(printWriter);

        // When
        jwtExceptionFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
        verify(response).setContentType("application/json; charset=UTF-8");
        ErrorResponseDto expectedErrorResponse = new ErrorResponseDto(ACCESS_TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED);
        verify(printWriter).write(new ObjectMapper().writeValueAsString(expectedErrorResponse));
    }

    @Test
    @DisplayName("JwtException 발생 시, 적절한 예외처리 객체 반환")
    void doFilterInternal_WithJwtException() throws IOException, ServletException {
        // Given
        doThrow(new JwtException("Invalid token"))
                .when(filterChain).doFilter(request, response);
        when(response.getWriter()).thenReturn(printWriter);

        // When
        jwtExceptionFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
        verify(response).setContentType("application/json; charset=UTF-8");
        ErrorResponseDto expectedErrorResponse = new ErrorResponseDto(INVALID_ACCESS_TOKEN, HttpStatus.UNAUTHORIZED);
        verify(printWriter).write(new ObjectMapper().writeValueAsString(expectedErrorResponse));
    }

    @Test
    @DisplayName("위 두 예외를 제외한 다른 예외 발생 시, 적절한 예외처리 객체 반환")
    void doFilterInternal_WithUnexpectedException() throws IOException, ServletException {
        // Given
        doThrow(new RuntimeException("Unexpected error"))
                .when(filterChain).doFilter(request, response);
        when(response.getWriter()).thenReturn(printWriter);

        // When
        jwtExceptionFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
        verify(response).setContentType("application/json; charset=UTF-8");
        ErrorResponseDto expectedErrorResponse = new ErrorResponseDto(UNEXPECTED_ERROR_OCCUR, HttpStatus.UNAUTHORIZED);
        verify(printWriter).write(new ObjectMapper().writeValueAsString(expectedErrorResponse));
    }

    @Test
    @DisplayName("예외 발생하지 않으면 동작 X")
    void doFilterInternal_WithoutException() throws IOException, ServletException {
        // Given
        doNothing().when(filterChain).doFilter(request, response);

        // When
        jwtExceptionFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response, never()).setStatus(anyInt());
        verify(response, never()).setContentType(anyString());
        verify(response, never()).getWriter();
    }
}


