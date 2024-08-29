package backend.yamukja.place.service;

import backend.yamukja.place.model.Place;
import backend.yamukja.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    @Transactional
    public void saveAllList(List<Place> places) {
        placeRepository.saveAll(places);
    }
}