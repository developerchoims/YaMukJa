package backend.yamukja.common;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@WithSecurityContext(factory = CustomSecurityContextFactory.class)
public @interface WithUserCustom {
    String id() default "1";
    String username() default "username";
    String role() default "USER";
}