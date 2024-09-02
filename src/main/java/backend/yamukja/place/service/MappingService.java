package backend.yamukja.place.service;

import backend.yamukja.common.service.RedisService;
import backend.yamukja.place.constant.Constants;
import backend.yamukja.place.model.Place;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MappingService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final PlaceService placeService;
    private final RedisService redisService;

    public List<Place> parseJsonResponse(String json, String urlTemplate) {
        List<Place> places = new ArrayList<>();
        Set<String> seenIds = new HashSet<>(); // 중복된 데이터 제거용 Set

        // URL에 맞는 Root Node 이름과 Redis Key 를 가져옵니다
        String[] info = Constants.URL_INFO_MAP.get(urlTemplate);
        String rootNodeName = info[0];
        String redisKey = info[1];

        try {
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode genrestrtNode = rootNode.path(rootNodeName);
            JsonNode rowNode = genrestrtNode.path(1).path("row");

            // Redis 업데이트
            JsonNode headNode = genrestrtNode.path(0).path("head");
            int totalCount = headNode.path(0).path("list_total_count").asInt();
            redisService.pushApiState(redisKey, totalCount);

            if (rowNode.isArray()) {
                for (JsonNode node : rowNode) {
                    String jsonNodeString = objectMapper.writeValueAsString(node);
                    try {
                        Place place = objectMapper.readValue(jsonNodeString, Place.class);
                        //json property 로 place 의 모든 field 가 set 된 후에 generate id 를 수행
                        place.generateId();
                        if (place != null && seenIds.add(place.getId())) {
                            places.add(place);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                throw new IllegalStateException(Constants.SQL_PARSE_ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        placeService.saveAllList(places);
        return places;
    }

    public int fetchTotalCount(String uri) {
        String jsonResponse = restTemplate.getForObject(uri, String.class);
        if (jsonResponse == null) {
            throw new IllegalStateException(Constants.SQL_PARSE_ERROR);
        }

        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode headNode = rootNode.path("Genrestrtchifood").path(0).path("head");
            return headNode.path(0).path("list_total_count").asInt();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

}
