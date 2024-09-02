package backend.yamukja.user.controllers;

import backend.yamukja.auth.service.TokenService;
import backend.yamukja.common.WithUserCustom;
import backend.yamukja.user.dto.UserResponse;
import backend.yamukja.user.entity.User;
import backend.yamukja.user.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @MockBean
    private UserService userService;

    @MockBean
    private TokenService tokenService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithUserCustom
    @DisplayName("유저 상세정보 반환")
    void getDetail() throws Exception {
        Long userId = 1L;
        User user = User.builder().id(1L).userId("testUser").geography(null).isLunchRecommend(false).build();
        UserResponse userResponse = new UserResponse(user);

        when(userService.getDetail(userId)).thenReturn(userResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("testUser"))
                .andDo(print());
    }
}
