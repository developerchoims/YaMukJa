package backend.yamukja.place.dto;

import backend.yamukja.place.model.Place;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaceDto {

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

    @JsonProperty("CLSBIZ_DE")
    private String clsbizDe;

    @JsonProperty("LOCPLC_AR")
    private String locplcAr;

    @JsonProperty("GRAD_FACLT_DIV_NM")
    private String gradFacltDivNm;

    @JsonProperty("MALE_ENFLPSN_CNT")
    private Integer maleEnflpsnCnt;

    @JsonProperty("YY")
    private String yy;

    @JsonProperty("MULTI_USE_BIZESTBL_YN")
    private String multiUseBizestblYn;

    @JsonProperty("GRAD_DIV_NM")
    private String gradDivNm;

    @JsonProperty("TOT_FACLT_SCALE")
    private String totFacltScale;

    @JsonProperty("FEMALE_ENFLPSN_CNT")
    private Integer femaleEnflpsnCnt;

    @JsonProperty("BSNSITE_CIRCUMFR_DIV_NM")
    private String bsnsiteCircumfrDivNm;

    @JsonProperty("SANITTN_INDUTYPE_NM")
    private String sanitnIndutypeNm;

    @JsonProperty("SANITTN_BIZCOND_NM")
    private String sanitnBizcondNm;

    @JsonProperty("TOT_EMPLY_CNT")
    private Integer totEmplyCnt;

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

    public Place toPlaceEntity() {
        String id = generateId(this.bizplcNm, this.refineLotnoAddr);
        return Place.builder()
                .id(id)
                .sigunNm(this.sigunNm)
                .sigunCd(this.sigunCd)
                .bizplcNm(this.bizplcNm)
                .licensgDe(this.licensgDe)
                .bsnStateNm(this.bsnStateNm)
                .sanitnBizcondNm(this.sanitnBizcondNm)
                .bsnsiteCircumfrDivNm(this.bsnsiteCircumfrDivNm)
                .refineRoadnmAddr(this.refineRoadnmAddr)
                .refineLotnoAddr(this.refineLotnoAddr)
                .refineZipCd(this.refineZipCd)
                .refineWgs84Lat(this.refineWgs84Lat)
                .refineWgs84Logt(this.refineWgs84Logt)
                .build();
    }

    private String generateId(String bizplcNm, String refineLotnoAddr) {
        return bizplcNm + "_" + refineLotnoAddr;
    }
}