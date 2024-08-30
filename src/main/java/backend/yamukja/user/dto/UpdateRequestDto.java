package backend.yamukja.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRequestDto {
    @NotNull
    private String userId;

    private Double latitude; //위도
    private Double longitude; //경도
    private Boolean isLunchRecommend; //점심 추천 서비스
}