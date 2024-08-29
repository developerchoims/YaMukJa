package backend.yamukja.auth.service;

import backend.yamukja.auth.dto.AuthTokens;
import backend.yamukja.auth.dto.LoginRequest;

public interface AuthService {
    AuthTokens login(LoginRequest loginRequest);
    String reissueToken(String refreshToken);
    void logOut(String refreshToken);
}
