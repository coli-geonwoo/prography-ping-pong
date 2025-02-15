package com.prography.ping_pong.service.userroom;

import com.prography.ping_pong.domain.room.Room;
import com.prography.ping_pong.domain.user.User;
import com.prography.ping_pong.domain.userroom.Team;
import com.prography.ping_pong.domain.userroom.UserRoom;
import com.prography.ping_pong.exception.custom.PingPongClientErrorException;
import com.prography.ping_pong.exception.errorcode.ClientErrorCode;
import com.prography.ping_pong.repository.UserRoomRepository;
import com.prography.ping_pong.service.room.TeamOrganizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserRoomService {

    private final UserRoomRepository userRoomRepository;
    private final TeamOrganizer teamOrganizer;

    @Transactional
    public void attend(User user, Room room) {
        long participateCount = userRoomRepository.countByRoomId(room.getId());
        if (!(canParticipate(user) && room.isAttendAble(participateCount))) {
            throw new PingPongClientErrorException(ClientErrorCode.INVALID_REQUEST);
        }
        Team team = organizeTeam(room);
        UserRoom userRoom = new UserRoom(user, room, team);
        userRoomRepository.save(userRoom);
    }

    private Team organizeTeam(Room room) {
        long roomId = room.getId();
        long firstOrderTeamCount = userRoomRepository.countByRoomIdAndTeam(roomId, Team.RED);
        long secondOrderTeamCount = userRoomRepository.countByRoomIdAndTeam(roomId, Team.BLUE);
        return teamOrganizer.organize(room.getRoomType(), firstOrderTeamCount, secondOrderTeamCount);
    }

    @Transactional(readOnly = true)
    public boolean canParticipate(User user) {
        boolean alreadyParticipated = userRoomRepository.existsByUserId(user.getId());
        return user.isActive() && !alreadyParticipated;
    }
}
