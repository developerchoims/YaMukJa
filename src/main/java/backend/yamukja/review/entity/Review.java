package backend.yamukja.review.entity;

import backend.yamukja.place.model.Place;
import backend.yamukja.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User FK
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Place FK
    @ManyToOne
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    // 점수 (0 ~ 5)
    @Column(nullable = false)
    private Integer score;

    // 평가 내용 (0 ~ 255자)
    @Column(length = 765)
    private String content;

}
