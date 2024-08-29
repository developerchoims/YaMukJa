package backend.yamukja.place.controller;

import backend.yamukja.place.model.Place;
import backend.yamukja.place.service.MappingService;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlaceController {

    private final MappingService mappingService;
    private final RestTemplate restTemplate;


    @Value("${open.api.key}")
    private String key;

    private static final String URL_TEMPLATE = "https://openapi.gg.go.kr/Genrestrtchifood";
    private static final String TYPE = "json"; // 호출 문서 타입
    private static final int PAGE_SIZE = 10;

    @GetMapping("/api/place-info")
    public List<Place> getFoodInfo(
            @RequestParam(value = "pIndex", defaultValue = "1") int pIndex,
            @RequestParam(value = "pSize", defaultValue = "100") int pSize,
            @RequestParam(value = "SIGUN_NM", required = false) String sigunNm,
            @RequestParam(value = "SIGUN_CD", required = false) String sigunCd) {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(URL_TEMPLATE)
                .queryParam("Key", key)
                .queryParam("Type", TYPE)
                .queryParam("pIndex", pIndex)
                .queryParam("pSize", PAGE_SIZE);

        if (sigunNm != null && !sigunNm.isEmpty()) {
            uriBuilder.queryParam("SIGUN_NM", sigunNm);
        }
        if (sigunCd != null && !sigunCd.isEmpty()) {
            uriBuilder.queryParam("SIGUN_CD", sigunCd);
        }

        ResponseEntity<String> response = restTemplate.getForEntity(uriBuilder.toUriString(), String.class);
        List<Place> places = mappingService.parseJsonResponse(response.getBody());

        return places;
    }
}