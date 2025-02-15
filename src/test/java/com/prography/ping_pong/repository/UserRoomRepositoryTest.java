package com.prography.ping_pong.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.prography.ping_pong.common.BaseRepositoryTest;
import com.prography.ping_pong.domain.userroom.Team;
import com.prography.ping_pong.domain.userroom.UserRoom;
import com.prography.ping_pong.domain.room.Room;
import com.prography.ping_pong.domain.room.RoomType;
import com.prography.ping_pong.domain.user.User;
import com.prography.ping_pong.domain.user.UserStatus;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserRoomRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private UserRoomRepository userRoomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @DisplayName("유저가 참여한 방이 있는지 조회할 수 있다")
    @Test
    void existsByUserId() {
        User user = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User savedUser = userRepository.save(user);
        Room dummy = new Room("room1", savedUser, RoomType.SINGLE);
        Room savedRoom = roomRepository.save(dummy);
        UserRoom userRoom = new UserRoom(user, dummy, Team.RED);
        userRoomRepository.save(userRoom);

        boolean exists = userRoomRepository.existsByUserId(user.getId());
        boolean nonExists = userRoomRepository.existsByUserId(1000L);

        assertAll(
                () -> assertThat(exists).isTrue(),
                () -> assertThat(nonExists).isFalse()
        );
    }

    @DisplayName("방에 참여한 인원이 몇명인지 카운트 할 수 있다")
    @Test
    void countByRoomId() {
        User user1 = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User user2 = new User(2L, "name2", "email2@email.com", UserStatus.ACTIVE);
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        Room dummy = new Room("room1", savedUser1, RoomType.SINGLE);
        Room savedRoom = roomRepository.save(dummy);

        UserRoom userRoom1 = new UserRoom(user1, dummy, Team.RED);
        UserRoom userRoom2 = new UserRoom(user2, dummy, Team.RED);
        UserRoom savedUserRoom1 = userRoomRepository.save(userRoom1);
        UserRoom savedUserRoom2 = userRoomRepository.save(userRoom2);

        long count = userRoomRepository.countByRoomId(savedRoom.getId());

        assertThat(count).isEqualTo(2);
    }

    @DisplayName("방의 팀별 인원 수를 알 수 있다")
    @Test
    void countByRoomIdAndTeam() {
        User user1 = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User user2 = new User(2L, "name2", "email2@email.com", UserStatus.ACTIVE);
        User user3 = new User(3L, "name3", "email3@email.com", UserStatus.ACTIVE);
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        User savedUser3 = userRepository.save(user3);

        Room dummy = new Room("room1", savedUser1, RoomType.DOUBLE);
        Room savedRoom = roomRepository.save(dummy);

        UserRoom redTeamUser1 = new UserRoom(savedUser1, dummy, Team.RED);
        UserRoom redTeamUser2 = new UserRoom(savedUser2, dummy, Team.RED);
        UserRoom blueTeamUser1 = new UserRoom(savedUser3, dummy, Team.BLUE);
        UserRoom savedUserRoom1 = userRoomRepository.save(redTeamUser1);
        UserRoom savedUserRoom2 = userRoomRepository.save(redTeamUser2);
        UserRoom savedUserRoom3 = userRoomRepository.save(blueTeamUser1);

        long redTeamUserCount = userRoomRepository.countByRoomIdAndTeam(savedRoom.getId(), Team.RED);
        long blueTeamUserCount = userRoomRepository.countByRoomIdAndTeam(savedRoom.getId(), Team.BLUE);

        assertAll(
                () -> assertThat(redTeamUserCount).isEqualTo(2L),
                () -> assertThat(blueTeamUserCount).isEqualTo(1L)
        );
    }

    @DisplayName("배치 쿼리로 전체 UserRoom 데이터를 삭제한다")
    @Test
    void deleteAllWithFlush() {
        User user = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User savedUser = userRepository.save(user);
        Room dummy = new Room("room1", savedUser, RoomType.SINGLE);
        Room savedRoom = roomRepository.save(dummy);
        UserRoom userRoom = new UserRoom(user, dummy, Team.RED);
        UserRoom savedUserRoom = userRoomRepository.save(userRoom);

        userRoomRepository.deleteAllWithFlush(List.of(savedUserRoom));

        long count = userRoomRepository.count();

        assertThat(count).isZero();
    }
}
