package com.prography.pingpong.common.fixture;

import com.prography.pingpong.domain.room.Room;
import com.prography.pingpong.domain.user.User;
import com.prography.pingpong.domain.userroom.Team;
import com.prography.pingpong.domain.userroom.UserRoom;
import com.prography.pingpong.repository.UserRoomRepository;
import org.springframework.stereotype.Component;

@Component
public class UserRoomGenerator {

    private final UserRoomRepository userRoomRepository;

    public UserRoomGenerator(UserRoomRepository userRoomRepository) {
        this.userRoomRepository = userRoomRepository;
    }

    public UserRoom generate(User user, Room room, Team team) {
        UserRoom userRoom = new UserRoom(user, room, team);
        return userRoomRepository.save(userRoom);
    }
}
