package backend.yamukja.place.service;

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

    public List<Place> fetchAndDeserialize(String uri) {
        String jsonResponse = restTemplate.getForObject(uri, String.class);
        if (jsonResponse == null) {
            throw new IllegalStateException(Constants.SQL_PARSE_ERROR);
        }
        return parseJsonResponse(jsonResponse);
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

    public List<Place> parseJsonResponse(String json) {
        List<Place> places = new ArrayList<>();
        Set<String> seenIds = new HashSet<>(); // 중복된 data 가 있어서 Set 을 이용해서 중복 제거

        try {
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode genrestrtchifoodNode = rootNode.path("Genrestrtchifood");
            JsonNode rowNode = genrestrtchifoodNode.path(1).path("row");

            if (rowNode.isArray()) {
                for (JsonNode node : rowNode) {
                    String jsonNodeString = objectMapper.writeValueAsString(node);
                    try {
                        Place place = objectMapper.readValue(jsonNodeString, Place.class);
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


    public List<Place> fetchAllPages(String urlTemplate, int totalCount) {
        int pIndex = 1;
        int pSize = 100;
        List<Place> allPlaces = new ArrayList<>();
        int totalPages = (totalCount + pSize - 1) / pSize;

        for (int i = 1; i <= totalPages; i++) {
            String pagedUri = urlTemplate + "&pIndex=" + i + "&pSize=" + pSize;
            List<Place> places = fetchAndDeserialize(pagedUri);
            allPlaces.addAll(places);
        }

        return allPlaces;
    }
}
