package com.prography.ping_pong.repository;

import com.prography.ping_pong.domain.room.Room;
import com.prography.ping_pong.domain.user.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface RoomRepository extends JpaRepository<Room, Long> {


    Page<Room> findAllByOrderByIdAsc(Pageable pageable);

    @Query("DELETE FROM Room room WHERE room IN :rooms")
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    void deleteAllWithFlush(List<Room> rooms);
}
