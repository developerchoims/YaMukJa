package backend.yamukja.review.repository;

import backend.yamukja.place.model.Place;
import backend.yamukja.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByPlaceOrderByIdDesc(Place place);
    List<Review> findByPlace(Place place);
}
