package com.prography.pingpong.repository;

import com.prography.pingpong.domain.user.User;
import com.prography.pingpong.repository.jdbc.UserJdbcRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Long>, UserJdbcRepository {

    Page<User> findAllByOrderByIdAsc(Pageable pageable);

    @Query("DELETE FROM User user WHERE user IN :users")
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    void deleteAllWithFlush(List<User> users);
}
