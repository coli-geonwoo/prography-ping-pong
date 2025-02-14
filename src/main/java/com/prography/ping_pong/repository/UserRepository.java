package com.prography.ping_pong.repository;

import com.prography.ping_pong.domain.user.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("DELETE FROM User user WHERE user IN :users")
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    void deleteAllWithFlush(List<User> users);
}
