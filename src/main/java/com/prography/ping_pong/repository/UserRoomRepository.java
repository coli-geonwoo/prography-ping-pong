package com.prography.ping_pong.repository;

import com.prography.ping_pong.domain.UserRoom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {

    boolean existsByUserId(long userId);

    long countByRoomId(long roomId);

    @Query("DELETE FROM UserRoom userRoom WHERE userRoom IN :userRooms")
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    void deleteAllWithFlush(List<UserRoom> userRooms);
}
