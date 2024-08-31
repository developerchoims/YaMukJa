package backend.yamukja.common;

import backend.yamukja.auth.model.UserCustom;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collections;

public class CustomSecurityContextFactory implements WithSecurityContextFactory<WithUserCustom> {
    @Override
    public SecurityContext createSecurityContext(WithUserCustom mockUserCustom) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UserCustom userCustom = new UserCustom(Long.valueOf(mockUserCustom.id()), mockUserCustom.username());

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                userCustom,
                null,
                Collections.singleton(new SimpleGrantedAuthority("USER"))
        );

        context.setAuthentication(token);
        return context;
    }
}
