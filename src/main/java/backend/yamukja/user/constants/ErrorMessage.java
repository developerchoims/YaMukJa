package backend.yamukja.user.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorMessage {
    public static final String ALREADY_JOINED_USER = "이미 존재하는 계정입니다.";
    public static final String NOT_EXIST_USER = "존재하지 않는 계정입니다.";
}
