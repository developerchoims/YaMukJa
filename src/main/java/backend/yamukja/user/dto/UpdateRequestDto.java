package backend.yamukja.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRequestDto {
    private String userId;
    private Double latitude; //위도
    private Double longitude; //경도
    private Boolean isLunchRecommend; //점심 추천 서비스
}