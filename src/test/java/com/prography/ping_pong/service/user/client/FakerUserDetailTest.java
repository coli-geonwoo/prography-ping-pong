package com.prography.ping_pong.service.user.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.prography.ping_pong.client.FakerUserDetail;
import com.prography.ping_pong.domain.user.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FakerUserDetailTest {

    private static final long USER_ACTIVE_THRESHOLD = 30;
    private static final long USER_WAIT_THRESHOLD = 60;

    @DisplayName("id가 ACTIVE 범위에 속하면 ACTIVE 로 초기화된다")
    @Test
    void initializeActive() {
        long activeId = USER_ACTIVE_THRESHOLD;

        FakerUserDetail userDetail = new FakerUserDetail(activeId, "이름", "doyoon87@lim.com");

        assertThat(userDetail.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @DisplayName("id가 WAIT 범위에 속하면 WAIT 로 초기화된다")
    @ParameterizedTest
    @ValueSource(longs = {USER_ACTIVE_THRESHOLD + 1, USER_WAIT_THRESHOLD})
    void initializeWait(long waitId) {
        FakerUserDetail userDetail = new FakerUserDetail(waitId, "이름", "doyoon87@lim.com");

        assertThat(userDetail.getStatus()).isEqualTo(UserStatus.WAIT);
    }

    @DisplayName("id가 NON_ACTIVE 범위에 속하면 NON_ACTIVE 로 초기화된다")
    @Test
    void initializeNoneActive() {
        long noneActiveId = USER_WAIT_THRESHOLD + 1;
        FakerUserDetail userDetail = new FakerUserDetail(noneActiveId, "이름", "doyoon87@lim.com");

        assertThat(userDetail.getStatus()).isEqualTo(UserStatus.NON_ACTIVE);
    }
}
