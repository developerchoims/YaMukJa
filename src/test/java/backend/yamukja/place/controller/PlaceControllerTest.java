package backend.yamukja.place.controller;

import backend.yamukja.auth.service.TokenService;
import backend.yamukja.common.WithUserCustom;
import backend.yamukja.common.config.SecurityConfig;
import backend.yamukja.common.service.RedisService;
import backend.yamukja.place.constant.Constants;
import backend.yamukja.place.model.Place;
import backend.yamukja.place.service.MappingService;
import backend.yamukja.place.service.PlaceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = PlaceController.class)
@ComponentScan(basePackages = {"backend.yamukja.auth.filter"})
@Import(SecurityConfig.class)
public class PlaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MappingService mappingService;

    @MockBean
    private RedisService redisService;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private PlaceService placeService;

    @Value("${open.api.key}")
    private String apiKey;


    @Test
    @WithUserCustom
    @DisplayName("중국 음식점 정보를 가져오는 API 테스트 - pIndex 1, pSize 1")
    void getChineseInfo() throws Exception {
        // JSON 응답 예제
        String jsonResponse = "[{\"id\": \"쌍홍루_경기도 의정부시 가능동 675-22\","
                + "\"rating\": null,"
                + "\"ratingCount\": null,"
                + "\"SIGUN_NM\": \"의정부시\","
                + "\"SIGUN_CD\": null,"
                + "\"BIZPLC_NM\": \"쌍홍루\","
                + "\"LICENSG_DE\": \"20000718\","
                + "\"BSN_STATE_NM\": \"영업\","
                + "\"BSNSITE_CIRCUMFR_DIV_NM\": \"기타\","
                + "\"SANITTN_BIZCOND_NM\": \"중국식\","
                + "\"REFINE_ROADNM_ADDR\": \"경기도 의정부시 의정로173번길 16 (가능동)\","
                + "\"REFINE_LOTNO_ADDR\": \"경기도 의정부시 가능동 675-22\","
                + "\"REFINE_ZIP_CD\": \"11675\","
                + "\"REFINE_WGS84_LAT\": \"37.7484226750\","
                + "\"REFINE_WGS84_LOGT\": \"127.0335010664\"}]";

        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(jsonResponse, HttpStatus.OK));

        List<Place> expectedPlaces = List.of(
                Place.builder()
                        .id("쌍홍루_경기도 의정부시 가능동 675-22")
                        .rating(null)
                        .ratingCount(null)
                        .sigunNm("의정부시")
                        .sigunCd(null)
                        .bizplcNm("쌍홍루")
                        .licensgDe("20000718")
                        .bsnStateNm("영업")
                        .bsnsiteCircumfrDivNm("기타")
                        .sanitnBizcondNm("중국식")
                        .refineRoadnmAddr("경기도 의정부시 의정로173번길 16 (가능동)")
                        .refineLotnoAddr("경기도 의정부시 가능동 675-22")
                        .refineZipCd("11675")
                        .refineWgs84Lat("37.7484226750")
                        .refineWgs84Logt("127.0335010664")
                        .build()
        );

        when(mappingService.parseJsonResponse(eq(jsonResponse), eq(Constants.CHINESE_URL_TEMPLATE))).thenReturn(expectedPlaces);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/chinese-info")
                        .param("pSize", "1")
                        .param("pIndex", "1")
                        .param("SIGUN_NM", "")
                        .param("SIGUN_CD", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("쌍홍루_경기도 의정부시 가능동 675-22"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].rating").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].ratingCount").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].SIGUN_NM").value("의정부시"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].SIGUN_CD").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].BIZPLC_NM").value("쌍홍루"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].LICENSG_DE").value("20000718"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].BSN_STATE_NM").value("영업"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].BSNSITE_CIRCUMFR_DIV_NM").value("기타"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].SANITTN_BIZCOND_NM").value("중국식"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].REFINE_ROADNM_ADDR").value("경기도 의정부시 의정로173번길 16 (가능동)"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].REFINE_LOTNO_ADDR").value("경기도 의정부시 가능동 675-22"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].REFINE_ZIP_CD").value("11675"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].REFINE_WGS84_LAT").value("37.7484226750"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].REFINE_WGS84_LOGT").value("127.0335010664"));
    }


    @Test
    @WithUserCustom
    @DisplayName("일식 음식점 정보를 가져오는 API 테스트 - pIndex 1, pSize 1")
    void getJapaneseInfo() throws Exception {

        String jsonResponse = "[{\"id\": \"미가라멘_경기도 수원시 장안구 율전동 433-81\","
                + "\"rating\": null,"
                + "\"ratingCount\": null,"
                + "\"SIGUN_NM\": \"수원시\","
                + "\"SIGUN_CD\": null,"
                + "\"BIZPLC_NM\": \"미가라멘\","
                + "\"LICENSG_DE\": \"20031208\","
                + "\"BSN_STATE_NM\": \"영업\","
                + "\"BSNSITE_CIRCUMFR_DIV_NM\": null,"
                + "\"SANITTN_BIZCOND_NM\": \"일식\","
                + "\"REFINE_ROADNM_ADDR\": \"경기도 수원시 장안구 화산로233번길 59 (율전동)\","
                + "\"REFINE_LOTNO_ADDR\": \"경기도 수원시 장안구 율전동 433-81\","
                + "\"REFINE_ZIP_CD\": \"16362\","
                + "\"REFINE_WGS84_LAT\": \"37.2971992231\","
                + "\"REFINE_WGS84_LOGT\": \"126.9702180375\"}]";

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(new ResponseEntity<>(jsonResponse, HttpStatus.OK));

        List<Place> expectedPlaces = List.of(
                Place.builder()
                        .id("미가라멘_경기도 수원시 장안구 율전동 433-81")
                        .rating(null)
                        .ratingCount(null)
                        .sigunNm("수원시")
                        .sigunCd(null)
                        .bizplcNm("미가라멘")
                        .licensgDe("20031208")
                        .bsnStateNm("영업")
                        .bsnsiteCircumfrDivNm(null)
                        .sanitnBizcondNm("일식")
                        .refineRoadnmAddr("경기도 수원시 장안구 화산로233번길 59 (율전동)")
                        .refineLotnoAddr("경기도 수원시 장안구 율전동 433-81")
                        .refineZipCd("16362")
                        .refineWgs84Lat("37.2971992231")
                        .refineWgs84Logt("126.9702180375")
                        .build()
        );

        when(mappingService.parseJsonResponse(eq(jsonResponse), eq(Constants.JAPANESE_URL_TEMPLATE)))
                .thenReturn(expectedPlaces);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/japanese-info")
                        .param("pSize", "1")
                        .param("pIndex", "1")
                        .param("SIGUN_NM", "")
                        .param("SIGUN_CD", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("미가라멘_경기도 수원시 장안구 율전동 433-81"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].rating").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].ratingCount").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].SIGUN_NM").value("수원시"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].SIGUN_CD").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].BIZPLC_NM").value("미가라멘"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].LICENSG_DE").value("20031208"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].BSN_STATE_NM").value("영업"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].BSNSITE_CIRCUMFR_DIV_NM").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].SANITTN_BIZCOND_NM").value("일식"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].REFINE_ROADNM_ADDR").value("경기도 수원시 장안구 화산로233번길 59 (율전동)"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].REFINE_LOTNO_ADDR").value("경기도 수원시 장안구 율전동 433-81"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].REFINE_ZIP_CD").value("16362"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].REFINE_WGS84_LAT").value("37.2971992231"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].REFINE_WGS84_LOGT").value("126.9702180375"));
    }

    @Test
    @WithUserCustom
    @DisplayName("패스트푸드 음식점 정보를 가져오는 API 테스트 - pIndex 1, pSize 1")
    void getFastfoodInfo() throws Exception {

        String jsonResponse = "[{\"id\": \"롯데리아 파주운정점_경기도 파주시 와동동 1301-3 대흥프라자 102~1호\","
                + "\"rating\": null,"
                + "\"ratingCount\": null,"
                + "\"SIGUN_NM\": \"파주시\","
                + "\"SIGUN_CD\": \"41480\","
                + "\"BIZPLC_NM\": \"롯데리아 파주운정점\","
                + "\"LICENSG_DE\": \"20100817\","
                + "\"BSN_STATE_NM\": \"영업\","
                + "\"BSNSITE_CIRCUMFR_DIV_NM\": null,"
                + "\"SANITTN_BIZCOND_NM\": \"패스트푸드\","
                + "\"REFINE_ROADNM_ADDR\": \"경기도 파주시 미래로 622, 대흥프라자 102~105호 (와동동)\","
                + "\"REFINE_LOTNO_ADDR\": \"경기도 파주시 와동동 1301-3 대흥프라자 102~1호\","
                + "\"REFINE_ZIP_CD\": \"10895\","
                + "\"REFINE_WGS84_LAT\": \"37.7327069364\","
                + "\"REFINE_WGS84_LOGT\": \"126.7509505626\"}]";

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(new ResponseEntity<>(jsonResponse, HttpStatus.OK));

        List<Place> expectedPlaces = List.of(
                Place.builder()
                        .id("롯데리아 파주운정점_경기도 파주시 와동동 1301-3 대흥프라자 102~1호")
                        .rating(null)
                        .ratingCount(null)
                        .sigunNm("파주시")
                        .sigunCd("41480")
                        .bizplcNm("롯데리아 파주운정점")
                        .licensgDe("20100817")
                        .bsnStateNm("영업")
                        .bsnsiteCircumfrDivNm(null)
                        .sanitnBizcondNm("패스트푸드")
                        .refineRoadnmAddr("경기도 파주시 미래로 622, 대흥프라자 102~105호 (와동동)")
                        .refineLotnoAddr("경기도 파주시 와동동 1301-3 대흥프라자 102~1호")
                        .refineZipCd("10895")
                        .refineWgs84Lat("37.7327069364")
                        .refineWgs84Logt("126.7509505626")
                        .build()
        );

        when(mappingService.parseJsonResponse(eq(jsonResponse), eq(Constants.FASTFOOD_URL_TEMPLATE)))
                .thenReturn(expectedPlaces);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/fastfood-info")
                        .param("pSize", "1")
                        .param("pIndex", "1")
                        .param("SIGUN_NM", "")
                        .param("SIGUN_CD", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("롯데리아 파주운정점_경기도 파주시 와동동 1301-3 대흥프라자 102~1호"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].rating").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].ratingCount").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].SIGUN_NM").value("파주시"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].SIGUN_CD").value("41480"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].BIZPLC_NM").value("롯데리아 파주운정점"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].LICENSG_DE").value("20100817"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].BSN_STATE_NM").value("영업"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].BSNSITE_CIRCUMFR_DIV_NM").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].SANITTN_BIZCOND_NM").value("패스트푸드"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].REFINE_ROADNM_ADDR").value("경기도 파주시 미래로 622, 대흥프라자 102~105호 (와동동)"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].REFINE_LOTNO_ADDR").value("경기도 파주시 와동동 1301-3 대흥프라자 102~1호"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].REFINE_ZIP_CD").value("10895"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].REFINE_WGS84_LAT").value("37.7327069364"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].REFINE_WGS84_LOGT").value("126.7509505626"));
    }
}