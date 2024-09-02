package backend.yamukja.place.repository;

import backend.yamukja.place.model.Place;
import backend.yamukja.place.model.UpdatePlaceRatingDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface PlaceRepository extends JpaRepository<Place, String> {

    @Modifying
    @Query("UPDATE Place p SET p.ratingCount = :#{#dto.ratingCount}, p.rating = :#{#dto.rating} WHERE p.id = :#{#dto.id}")
    void updateRating(@Param("dto") UpdatePlaceRatingDto updatePlaceRatingDto);
}
