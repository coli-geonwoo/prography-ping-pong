package com.prography.ping_pong.dto.response.user;

import com.prography.ping_pong.domain.user.User;
import java.util.List;
import org.springframework.data.domain.Page;

public record UserPageResponse(
        long totalElements,
        long totalPages,
        List<UserFindResponse> userList
) {

    public UserPageResponse(Page<User> users) {
        this(users.getTotalElements(), users.getTotalPages(), mapToUserFindResponses(users.toList()));
    }

    private static List<UserFindResponse> mapToUserFindResponses(List<User> users) {
        return users.stream()
                .map(UserFindResponse::new)
                .toList();
    }
}
