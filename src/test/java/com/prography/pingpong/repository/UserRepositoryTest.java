package com.prography.pingpong.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.prography.pingpong.common.BaseRepositoryTest;
import com.prography.pingpong.domain.user.User;
import com.prography.pingpong.domain.user.UserStatus;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

class UserRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @DisplayName("모든 유저를 배치 쿼리로 삭제한다")
    @Test
    void deleteAllWithFlush() {
        User user1 = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User user2 = new User(2L, "name2", "email2@email.com", UserStatus.ACTIVE);
        userRepository.save(user1);
        userRepository.save(user2);

        userRepository.deleteAllWithFlush(Arrays.asList(user1, user2));

        long count = userRepository.count();
        assertThat(count).isZero();
    }

    @DisplayName("페이징된 멤버를 조회한다")
    @Test
    void findAllByOrderByIdAsc() {
        PageRequest pageRequest = PageRequest.of(1, 1);
        User user1 = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User user2 = new User(2L, "name2", "email2@email.com", UserStatus.ACTIVE);
        userRepository.save(user1);
        userRepository.save(user2);

        Page<User> users = userRepository.findAllByOrderByIdAsc(pageRequest);

        assertAll(
                () -> assertThat(users.toList()).hasSize(1),
                () -> assertThat(users.toList().get(0).getId()).isEqualTo(user2.getId())
        );
    }
}
