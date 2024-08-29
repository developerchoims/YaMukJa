package backend.yamukja.place.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
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

    public void generateId() {
        this.id = bizplcNm + "_" + refineLotnoAddr;
    }

}
