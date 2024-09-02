package backend.yamukja.place.service;

import backend.yamukja.place.constant.Constants;
import backend.yamukja.place.model.Place;
import backend.yamukja.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private static final String DISTANCE = "distance";
    private static final String RATING = "rating";

    @Transactional
    public List<Place> saveAllList(List<Place> places) {
        List<String> placeIds = places.stream().map(Place::getId).collect(Collectors.toList());

        Set<String> existingIds = placeRepository.findAllById(placeIds)
                                    .stream().map(Place::getId).collect(Collectors.toSet());

        List<Place> uniquePlaces = places.stream()
                .filter(place -> !existingIds.contains(place.getId()) && "영업".equals(place.getBsnStateNm()))
                .collect(Collectors.toList());

        return placeRepository.saveAll(uniquePlaces);
    }

    /**
     *
     * 현재 위도, 경도 기준으로 range 안에 있는 맛집 리스트 반환
     * @param lat 위도
     * @param lon 경도
     * @param range 사용자 요청 주소(필터링할 거리, 즉 lat, lon 과의 거리를 의미)
     * @param order 정렬 방식
     * @return 조건에 맞는 맛집 리스트
     */
    public List<Place> getPlaceList(String lat, String lon, Double range, String order) {
        // 전체 맛집 리스트를 일단 가져온다
        List<Place> placeList = placeRepository.findAll();

        double doubleLat = Double.parseDouble(lat);
        double doubleLon = Double.parseDouble(lon);

        // 해시맵을 사용해서 key 는 place, value 는 거리를 넣어준다
        Map<Place, Double> placeToDistance = new HashMap<>();
        for (Place place : placeList) {
            Double distance = calculateDistance(
                    doubleLat,
                    doubleLon,
                    Double.parseDouble(place.getRefineWgs84Lat()),
                    Double.parseDouble(place.getRefineWgs84Logt()));
            placeToDistance.put(place, distance);
        }

        // range 안의 place 만 필터링한다
        Map<Place, Double> filteredPlaceToDistance = new HashMap<>();
        placeToDistance.forEach((place, distance) -> {
            if (distance < range) filteredPlaceToDistance.put(place, distance);
        });
        System.out.println(filteredPlaceToDistance.size());
        // 정렬 방식에 따라
        if (order.equals(DISTANCE)) {
            // 거리순 정렬일 경우, place, distance 쌍 리스트를 만들어서 정렬 및 반환
            List<Pair<Place, Double>> filteredPlaceList = new ArrayList<>();
            filteredPlaceToDistance.forEach((place, distance) -> {
                filteredPlaceList.add(Pair.of(place, distance));
            });

            filteredPlaceList.sort(Comparator.comparingDouble(Pair::getSecond));
            return filteredPlaceList.stream().map(Pair::getFirst).toList();
        } else if (order.equals(RATING)) {
            // 평점 순 정렬일 경우, 따로 변환 없이 정렬 후 반환
            List<Place> filteredPlaceList = new ArrayList<>();
            filteredPlaceToDistance.forEach((place, distance) -> {
                filteredPlaceList.add(place);
            });
            // 평점순은 내림차순 정렬
            filteredPlaceList.sort((o1, o2) -> Double.compare(o2.getRating(), o1.getRating()));
            return filteredPlaceList;
        } else {
            throw new IllegalStateException(Constants.ORDER_VALUE_ERROR);
        }
    }

    /**
     * 두 좌표 간의 거리 반환
     * @param lat1 지점1의 위도
     * @param lon1 지점1의 경도
     * @param lat2 지점2의 위도
     * @param lon2 지점2의 경도
     * @return 지점1과 지점2 사이의 거리
     */
    private Double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}