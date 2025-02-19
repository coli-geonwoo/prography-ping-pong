package com.prography.pingpong.common.fixture;

import com.prography.pingpong.domain.user.User;
import com.prography.pingpong.domain.user.UserStatus;
import com.prography.pingpong.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserGenerator {

    private final UserRepository userRepository;

    public UserGenerator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User generate(long fakerId, UserStatus status) {
        User user = new User(fakerId, "name", "email@email.com", status);
        return userRepository.save(user);
    }
}
