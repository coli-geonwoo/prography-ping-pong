package com.prography.pingpong.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.prography.pingpong.common.BaseServiceTest;
import com.prography.pingpong.domain.user.UserStatus;
import com.prography.pingpong.dto.response.user.UserPageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

class UserFacadeServiceTest extends BaseServiceTest {

    @Autowired
    private UserFacadeService userFacadeService;

    @DisplayName("페이징된 유저 목록을 조회한다")
    @Test
    void findAllUsers() {
        userGenerator.generate(1L, UserStatus.ACTIVE);
        userGenerator.generate(2L, UserStatus.ACTIVE);

        UserPageResponse response = userFacadeService.findAll(Pageable.ofSize(1).withPage(1));

        assertAll(
                () -> assertThat(response.totalPages()).isEqualTo(2L),
                () -> assertThat(response.totalElements()).isEqualTo(2L),
                () -> assertThat(response.userList()).hasSize(1),
                () -> assertThat(response.userList().get(0).id()).isEqualTo(2L)
        );
    }
}
