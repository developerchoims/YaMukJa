package backend.yamukja.user.entity;

import backend.yamukja.review.entity.Review;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;

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

    @Column(columnDefinition = "POINT SRID 4326")
    private Point geography;

    @Column(nullable = false)
    private Boolean isLunchRecommend;

    // Review와의 일대다 관계 설정
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public void updateUser(Point geography, Boolean isLunchRecommend){
        if (geography != null) this.geography = geography;
        if (isLunchRecommend != null) this.isLunchRecommend = isLunchRecommend;
    }
}
