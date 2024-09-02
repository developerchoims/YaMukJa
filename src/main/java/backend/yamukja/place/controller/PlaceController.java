package backend.yamukja.place.controller;

import backend.yamukja.auth.model.UserCustom;
import backend.yamukja.common.service.RedisService;
import backend.yamukja.place.constant.Constants;
import backend.yamukja.place.model.Place;
import backend.yamukja.place.service.MappingService;
import backend.yamukja.place.service.PlaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PlaceController {

    private final MappingService mappingService;
    private final RestTemplate restTemplate;
    private final PlaceService placeService;
    private final RedisService redisService;


    @Value("${open.api.key}")
    private String key;

    private static final String TYPE = "json"; // 호출 문서 타입

    /**
     * quartz scheduler 에서 사용하는 api 로 자동화를 위해 url param 을 추가하였습니다.
     * @param pSize
     * @param sigunNm
     * @param sigunCd
     * @param url
     * @return
     */
    @PostMapping("/api/place-info")
    public List<Place> getFoodInfo(
            @RequestParam(value = "pSize", defaultValue = "100") int pSize,
            @RequestParam(value = "SIGUN_NM", required = false) String sigunNm,
            @RequestParam(value = "SIGUN_CD", required = false) String sigunCd,
            @RequestParam(value = "url") String url) {

        // Redis에서 현재 페이지 번호를 나타내는 pIndex 가져오기
        String[] info = Constants.URL_INFO_MAP.get(url);
        int currentIndex = redisService.getCurrentIndex(info[1]);
        log.info("URL :: {} place-info current INDEX :: {}", url, currentIndex);

        // URI 빌드
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("Key", key)
                .queryParam("Type", TYPE)
                .queryParam("pIndex", currentIndex)
                .queryParam("pSize", pSize);

        if (sigunNm != null && !sigunNm.isEmpty()) {
            uriBuilder.queryParam("SIGUN_NM", sigunNm);
        }
        if (sigunCd != null && !sigunCd.isEmpty()) {
            uriBuilder.queryParam("SIGUN_CD", sigunCd);
        }

        // RestTemplate을 사용하여 API 요청 보내기
        ResponseEntity<String> response = restTemplate.getForEntity(uriBuilder.toUriString(), String.class);
        List<Place> places = mappingService.parseJsonResponse(response.getBody(), url);

        // 현재 pIndex를 증가시키고 Redis에 업데이트
        redisService.setCurrentIndex(info[1], currentIndex + 1);

        // API 응답 반환
        return places;
    }

    @GetMapping("/api/place-list")
    public List<Place> getPlaceList(
            @RequestParam("lat") String lat,
            @RequestParam("lon") String lon,
            @RequestParam("range") Double range,
            @RequestParam(value = "order", defaultValue = "distance") String order  // 정렬 기능: rating(평점순), distance(거리순) 중 택 1
    ) {
        return placeService.getPlaceList(lat, lon, range, order);
    }

    /**
     * 수동으로 경기도_일반음식점(중국식) 현황 자료 데이터를 저장하는 api 입니다.
     * /api/chinese-info default 로 api 요청하시면 1번째 페이지의 100개의 데이터가 저장됩니다.
     * @param pSize
     * @param pIndex
     * @param sigunNm 시군명
     * @param sigunCd 시군코드
     * @return
     */
    @PostMapping("/api/chinese-info")
    public List<Place> getChinesePlaceInfo(
            @RequestParam(value = "pSize", defaultValue = "100") int pSize,
            @RequestParam(value = "pIndex", defaultValue = "1") int pIndex,
            @RequestParam(value = "SIGUN_NM", required = false) String sigunNm,
            @RequestParam(value = "SIGUN_CD", required = false) String sigunCd,
            @AuthenticationPrincipal UserCustom userCustom) {

        // URI 빌드
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(Constants.CHINESE_URL_TEMPLATE)
                .queryParam("Key", key)
                .queryParam("Type", TYPE)
                .queryParam("pIndex", pIndex)
                .queryParam("pSize", pSize);

        if (sigunNm != null && !sigunNm.isEmpty()) {
            uriBuilder.queryParam("SIGUN_NM", sigunNm);
        }
        if (sigunCd != null && !sigunCd.isEmpty()) {
            uriBuilder.queryParam("SIGUN_CD", sigunCd);
        }

        // RestTemplate을 사용하여 API 요청 보내기
        ResponseEntity<String> response = restTemplate.getForEntity(uriBuilder.toUriString(), String.class);
        return mappingService.parseJsonResponse(response.getBody(), Constants.CHINESE_URL_TEMPLATE);
    }

    /**
     * 수동으로 경기도_일반음식점(일식) 현황 자료 데이터를 저장하는 api 입니다.
     * /api/japanese-info default 로 api 요청하시면 1번째 페이지의 100개의 데이터가 저장됩니다.
     * @param pSize
     * @param pIndex
     * @param sigunNm 시군명
     * @param sigunCd 시군코드
     * @return
     */
    @PostMapping("/api/japanese-info")
    public List<Place> getJapanesePlaceInfo(
            @RequestParam(value = "pSize", defaultValue = "100") int pSize,
            @RequestParam(value = "pIndex", defaultValue = "1") int pIndex,
            @RequestParam(value = "SIGUN_NM", required = false) String sigunNm,
            @RequestParam(value = "SIGUN_CD", required = false) String sigunCd,
            @AuthenticationPrincipal UserCustom userCustom) {

        // URI 빌드
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(Constants.JAPANESE_URL_TEMPLATE)
                .queryParam("Key", key)
                .queryParam("Type", TYPE)
                .queryParam("pIndex", pIndex)
                .queryParam("pSize", pSize);

        if (sigunNm != null && !sigunNm.isEmpty()) {
            uriBuilder.queryParam("SIGUN_NM", sigunNm);
        }
        if (sigunCd != null && !sigunCd.isEmpty()) {
            uriBuilder.queryParam("SIGUN_CD", sigunCd);
        }

        // RestTemplate을 사용하여 API 요청 보내기
        ResponseEntity<String> response = restTemplate.getForEntity(uriBuilder.toUriString(), String.class);
        return mappingService.parseJsonResponse(response.getBody(),Constants.JAPANESE_URL_TEMPLATE);
    }

    /**
     * 수동으로 경기도_일반음식점(패스트푸드) 현황 자료 데이터를 저장하는 api 입니다.
     * /api/fastfood-info default 로 api 요청하시면 1번째 페이지의 100개의 데이터가 저장됩니다.
     * @param pSize
     * @param pIndex
     * @param sigunNm 시군명
     * @param sigunCd 시군코드
     * @return
     */
    @PostMapping("/api/fastfood-info")
    public List<Place> getFastfoodPlaceInfo(
            @RequestParam(value = "pSize", defaultValue = "100") int pSize,
            @RequestParam(value = "pIndex", defaultValue = "1") int pIndex,
            @RequestParam(value = "SIGUN_NM", required = false) String sigunNm,
            @RequestParam(value = "SIGUN_CD", required = false) String sigunCd,
            @AuthenticationPrincipal UserCustom userCustom) {

        // URI 빌드
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(Constants.FASTFOOD_URL_TEMPLATE)
                .queryParam("Key", key)
                .queryParam("Type", TYPE)
                .queryParam("pIndex", pIndex)
                .queryParam("pSize", pSize);

        if (sigunNm != null && !sigunNm.isEmpty()) {
            uriBuilder.queryParam("SIGUN_NM", sigunNm);
        }
        if (sigunCd != null && !sigunCd.isEmpty()) {
            uriBuilder.queryParam("SIGUN_CD", sigunCd);
        }

        // RestTemplate을 사용하여 API 요청 보내기
        ResponseEntity<String> response = restTemplate.getForEntity(uriBuilder.toUriString(), String.class);
        return mappingService.parseJsonResponse(response.getBody(), Constants.FASTFOOD_URL_TEMPLATE);
    }

}