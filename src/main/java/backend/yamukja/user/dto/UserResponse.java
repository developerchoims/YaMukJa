package backend.yamukja.user.dto;

import backend.yamukja.user.entity.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.locationtech.jts.geom.Point;

@Getter
@EqualsAndHashCode
public class UserResponse {
    private Long id;
    private String username;
    private Point geography;
    private Boolean isLunchRecommend;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUserId();
        this.geography = user.getGeography();
        this.isLunchRecommend = user.getIsLunchRecommend();
    }
}
