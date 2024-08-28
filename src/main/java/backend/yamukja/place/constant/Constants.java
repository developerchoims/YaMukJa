package backend.yamukja.place.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final String API_REQUEST_INFO_MISSING = "필수 값이 누락되어 있습니다. 요청인자를 참고 하십시오.";
    public static final String API_KEY_INVALID = "인증키가 유효하지 않습니다. 인증키가 없는 경우, 홈페이지에서 인증키를 신청하십시오.";
    public static final String SQL_PARSE_ERROR = "SQL 문장 오류 입니다. 지속적으로 발생시 홈페이지로 문의(Q&A) 바랍니다.";
}