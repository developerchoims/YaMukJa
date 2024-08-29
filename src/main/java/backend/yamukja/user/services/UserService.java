package backend.yamukja.user.services;

import backend.yamukja.common.exception.GeneralException;
import backend.yamukja.user.constants.ErrorMessage;
import backend.yamukja.user.dto.JoinRequestDto;
import backend.yamukja.user.dto.UpdateRequestDto;
import backend.yamukja.user.entity.User;
import backend.yamukja.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

    private final BCryptPasswordEncoder encoder;
    private final UserRepository userRepository;

    @Transactional
    public void join(JoinRequestDto request){
        // 계정 중복 확인
        if (userRepository.existsByUserId(request.getUserId())) {
            throw new GeneralException(ErrorMessage.ALREADY_JOINED_USER, HttpStatus.CONFLICT);
        }

        // 유저 생성
        User newUser = User.builder()
                .userId(request.getUserId())
                .password(encoder.encode(request.getPassword()))
                .isLunchRecommend(false)
                .build();

        userRepository.save(newUser);
    }

    @Transactional
    public void update(UpdateRequestDto request){
        // 계정 확인
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new GeneralException(ErrorMessage.NOT_EXIST_USER, HttpStatus.NOT_FOUND));

        // 위치 정보 생성
        Point point = createPoint(request.getLatitude(), request.getLongitude());

        user.updateUser(point, request.getIsLunchRecommend());
    }

    public Point createPoint(double latitude, double longitude) {
        // 경도, 위도로 Point 생성
        return GEOMETRY_FACTORY.createPoint(new Coordinate(longitude, latitude));
    }
}
