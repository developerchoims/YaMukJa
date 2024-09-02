package backend.yamukja.place.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatePlaceRatingDto {
    private String id;
    private int ratingCount;
    private Double rating;
}
