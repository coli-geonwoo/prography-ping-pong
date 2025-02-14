package com.prography.ping_pong.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.prography.ping_pong.common.BaseServiceTest;
import com.prography.ping_pong.domain.user.User;
import com.prography.ping_pong.domain.user.UserStatus;
import com.prography.ping_pong.dto.response.user.UserPageResponse;
import com.prography.ping_pong.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

class UserServiceTest extends BaseServiceTest {

    @Autowired
    private UserService userService;

    @DisplayName("페이징된 유저 목록을 조회한다")
    @Test
    void findAllUsers() {
        User user1 = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User user2 = new User(2L, "name2", "email2@email.com", UserStatus.ACTIVE);
        userRepository.save(user1);
        userRepository.save(user2);

        UserPageResponse response = userService.findAll(Pageable.ofSize(1).withPage(1));

        assertAll(
                () -> assertThat(response.totalPages()).isEqualTo(2L),
                () -> assertThat(response.totalElements()).isEqualTo(2L),
                () -> assertThat(response.userList()).hasSize(1),
                () -> assertThat(response.userList().get(0).id()).isEqualTo(2L)
        );
    }
}
