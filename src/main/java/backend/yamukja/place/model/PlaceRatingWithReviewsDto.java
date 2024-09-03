package backend.yamukja.place.model;

import backend.yamukja.review.model.ReviewResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PlaceRatingWithReviewsDto {
    private Double rating;
    private Integer ratingCount;
    private List<ReviewResponseDto> reviews;
}