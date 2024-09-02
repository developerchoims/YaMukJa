package backend.yamukja.review.model;

import lombok.Data;

@Data
public class CreateReviewDto {
    private String userId;
    private String placeCode;
    private int score;
    private String content;
}
