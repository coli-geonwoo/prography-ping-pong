package com.prography.ping_pong.service.user.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.prography.ping_pong.client.FakerUserDetail;
import com.prography.ping_pong.client.FakerUserDetails;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FakerUserDetailsTest {

    @DisplayName("fakerId 순으로 오름차순 정렬된다")
    @Test
    void orderByFakerId() {
        FakerUserDetail userDetail1 = new FakerUserDetail(1L, "이름1", "email1@email.com");
        FakerUserDetail userDetail2 = new FakerUserDetail(2L, "이름2", "email2@email.com");
        FakerUserDetail userDetail3 = new FakerUserDetail(3L, "이름3", "email3@email.com");
        List<FakerUserDetail> userDetails = Arrays.asList(userDetail3, userDetail1, userDetail2);

        FakerUserDetails actual = new FakerUserDetails(userDetails);

        assertThat(actual.getFakerUserDetails())
                .containsExactly(userDetail1, userDetail2, userDetail3);
    }
}
