package backend.yamukja.place.constant;

import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class Constants {
    public static final String INVALID_URL = "올바른 URL 형식이 아닙니다.";
    public static final String API_KEY_INVALID = "인증키가 유효하지 않습니다. 인증키가 없는 경우, 홈페이지에서 인증키를 신청하십시오.";
    public static final String SQL_PARSE_ERROR = "SQL 문장 오류 입니다. 지속적으로 발생시 홈페이지로 문의(Q&A) 바랍니다.";
    public static final String ORDER_VALUE_ERROR = "잘못된 정렬 방법입니다. 거리순 또는 평점순으로 요청하세요.";
    public static final String CHINESE_URL_TEMPLATE = "https://openapi.gg.go.kr/Genrestrtchifood";

    public static final String FASTFOOD_URL_TEMPLATE = "https://openapi.gg.go.kr/Genrestrtfastfood";

    public static final String JAPANESE_URL_TEMPLATE = "https://openapi.gg.go.kr/Genrestrtjpnfood";

    public static final Map<String, String[]> URL_INFO_MAP = Map.of(
            CHINESE_URL_TEMPLATE, new String[]{"Genrestrtchifood", "Chinese"},
            FASTFOOD_URL_TEMPLATE, new String[]{"Genrestrtfastfood", "Fastfood"},
            JAPANESE_URL_TEMPLATE, new String[]{"Genrestrtjpnfood", "Japanese"}
    );
}