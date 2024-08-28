package backend.yamukja.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private Boolean isLunchRecommend = false;
}
