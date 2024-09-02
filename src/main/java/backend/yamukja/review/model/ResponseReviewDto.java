package backend.yamukja.review.model;

import lombok.Data;

@Data
public class ResponseReviewDto {
    private Long id;
    private String userId;
    private String bizplcNm;
    //평가에 참여한 사람 수
    private int ratingCount;
    //전체 평점
    private String rating;
    //현 유저가 준 평점
    private String score;
    private String comment;
}
