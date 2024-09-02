package backend.yamukja.place.model;

import backend.yamukja.review.entity.Review;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Place {

    @Id
    private String id;

    @JsonProperty("SIGUN_NM")
    private String sigunNm;

    @JsonProperty("SIGUN_CD")
    private String sigunCd;

    @JsonProperty("BIZPLC_NM")
    private String bizplcNm;

    @JsonProperty("LICENSG_DE")
    private String licensgDe;

    @JsonProperty("BSN_STATE_NM")
    private String bsnStateNm;

    @JsonProperty("SANITTN_BIZCOND_NM")
    private String sanitnBizcondNm;

    @JsonProperty("BSNSITE_CIRCUMFR_DIV_NM")
    private String bsnsiteCircumfrDivNm;

    @JsonProperty("REFINE_ROADNM_ADDR")
    private String refineRoadnmAddr;

    @JsonProperty("REFINE_LOTNO_ADDR")
    private String refineLotnoAddr;

    @JsonProperty("REFINE_ZIP_CD")
    private String refineZipCd;

    @JsonProperty("REFINE_WGS84_LAT")
    private String refineWgs84Lat;

    @JsonProperty("REFINE_WGS84_LOGT")
    private String refineWgs84Logt;

    // 추가필드 - 평점(double 타입, 초기 값은 0.0 이며, 맛집이 받은 모든 평가의 평균)
    @ColumnDefault("0.0")
    private Double rating;

    // 총 평점 제출 개수(맛집 평가 API 계산용)
    @ColumnDefault("0")
    private Integer ratingCount;

    // Review와의 일대다 관계 설정
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public void generateId() {
        this.id = bizplcNm + "_" + refineLotnoAddr;
    }
}
