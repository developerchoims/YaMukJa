package backend.yamukja.review.service;

import backend.yamukja.place.model.Place;
import backend.yamukja.place.model.UpdatePlaceRatingDto;
import backend.yamukja.place.repository.PlaceRepository;
import backend.yamukja.review.entity.Review;
import backend.yamukja.review.model.CreateReviewDto;
import backend.yamukja.review.model.ReviewResponseDto;
import backend.yamukja.review.repository.ReviewRepository;
import backend.yamukja.user.entity.User;
import backend.yamukja.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public ResponseEntity<String> createScore(CreateReviewDto reviewDto) {
        // review create를 위한 user, place 정보 불러오기
        User user = userRepository.findByUserId(reviewDto.getUserId())
                                    .orElseThrow(() -> new EntityNotFoundException("dd"));
        Place place = placeRepository.findById(reviewDto.getPlaceCode())
                                    .orElseThrow(() -> new EntityNotFoundException("dd"));
        try{
            // review build 후 create
            Review review = Review.builder()
                    .user(user)
                    .place(place)
                    .score(reviewDto.getScore())
                    .content(reviewDto.getContent())
                    .build();
            reviewRepository.save(review);
            // Place Rating, Ratingcount Update
            updateRating(place, review);
            return ResponseEntity.ok("Success");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public void updateRating (Place place, Review review) {
        //업데이트될 ratingCount, rating
        int updatingRatingCount = place.getRatingCount() + 1;
        Double updatingRating = (place.getRating() + review.getScore()) / updatingRatingCount;
        //Update시킬 Dto
        UpdatePlaceRatingDto dto = UpdatePlaceRatingDto.builder()
                                                        .id(place.getId())
                                                        .ratingCount(updatingRatingCount)
                                                        .rating(updatingRating)
                                                        .build();
        placeRepository.updateRating(dto);
    }


    public void updateRating(Place place) {
        List<Review> reviews = reviewRepository.findByPlace(place);

        double averageRating = reviews.stream()
                .mapToInt(Review::getScore)
                .average()
                .orElse(0.0);

        place.setRating(Double.valueOf(String.format("%.1f", averageRating)));

        placeRepository.save(place);
    }

}
