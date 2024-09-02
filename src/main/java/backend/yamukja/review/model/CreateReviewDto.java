package backend.yamukja.review.model;

import lombok.Data;

@Data
public class CreateReviewDto {
    private Long id;
    private Long userCode;
    private String placeCode;
    private int score;
    private String content;

}
