package backend.yamukja.user.entity;

import backend.yamukja.user.dto.UpdateRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false) // unique 제약 조건 추가
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(columnDefinition = "GEOMETRY")
    private Point geography;

    @Column(nullable = false)
    private Boolean isLunchRecommend;

    public void updateUser(Point geography, Boolean isLunchRecommend){
        this.geography = geography;
        this.isLunchRecommend = isLunchRecommend;
    }
}
