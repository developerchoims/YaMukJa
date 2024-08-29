package backend.yamukja.auth.dto;

import lombok.Getter;
import lombok.ToString;

/**
 * 로그인 요청 DTO
 */
@Getter
@ToString
public class LoginRequest {
    private String username;
    private String password;
}
