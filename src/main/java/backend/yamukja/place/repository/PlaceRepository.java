package backend.yamukja.place.repository;

import backend.yamukja.place.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, String> {
    @Query("SELECT p FROM Place p WHERE p.id IN :ids")
    List<Place> findAllById(@Param("ids") List<String> ids);
}