package backend.yamukja.review.model;

import lombok.*;


@Data
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDto {

    private Long id;
    private String userName;
    private Integer score;
    private String content;
}