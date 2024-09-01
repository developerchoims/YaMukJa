package backend.yamukja.user.services;

import backend.yamukja.user.constants.ErrorMessage;
import backend.yamukja.user.dto.UserResponse;
import backend.yamukja.user.entity.User;
import backend.yamukja.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("사용자 상세 정보: 성공 시나리오")
    void getDetail_WhenUserAuthenticated_ShouldReturnUserDetailResponse() {
        Long userId = 1L;
        User user = User.builder().id(userId).userId("testUser").geography(null).isLunchRecommend(false).build();
        UserResponse expectedResponse = new UserResponse(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponse actualResponse = userService.getDetail(userId);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("사용자 상세 정보: DB에 해당 유저가 없는 경우")
    void getDetail_WhenUserNotFound_ShouldThrowNoSuchElementException() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NoSuchElementException e = assertThrows(NoSuchElementException.class, () -> userService.getDetail(userId));
        assertEquals(ErrorMessage.USER_NOT_FOUND, e.getMessage());
    }
}