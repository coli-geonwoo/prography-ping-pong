package com.prography.ping_pong.service.userroom;

import com.prography.ping_pong.domain.room.Room;
import com.prography.ping_pong.domain.room.RoomType;
import com.prography.ping_pong.domain.user.User;
import com.prography.ping_pong.domain.userroom.Team;
import com.prography.ping_pong.domain.userroom.UserRoom;
import com.prography.ping_pong.exception.custom.PingPongClientErrorException;
import com.prography.ping_pong.exception.errorcode.ClientErrorCode;
import com.prography.ping_pong.repository.UserRoomRepository;
import com.prography.ping_pong.service.room.TeamOrganizer;
import java.util.List;
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
        List<UserRoom> allRoomUsers = userRoomRepository.findAllByRoomId(room.getId());
        long participateCount = allRoomUsers.size();
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
        boolean alreadyParticipated = userRoomRepository.findByUserId(user.getId()).isPresent();
        return user.isActive() && !alreadyParticipated;
    }

    @Transactional(readOnly = true)
    public UserRoom findByUserIdAndRoomId(long userId, long roomId) {
        return userRoomRepository.findByUserIdAndRoomId(userId, roomId)
                .orElseThrow(() -> new PingPongClientErrorException(ClientErrorCode.INVALID_REQUEST));
    }

    @Transactional
    public void changeTeam(long userId, long roomId) {
        UserRoom userRoom = findByUserIdAndRoomId(userId, roomId);
        Team oppositeTeam = userRoom.getOppositeTeam();
        long oppositeTeamUserCount = userRoomRepository.countByRoomIdAndTeam(roomId, oppositeTeam);

        if(!userRoom.canChangeTeam(oppositeTeamUserCount)) {
            throw new PingPongClientErrorException(ClientErrorCode.INVALID_REQUEST);
        }
        userRoom.changeTeam();
    }

    @Transactional
    public void exitAllRoomUsers(long roomId) {
        List<UserRoom> allRoomUsers = userRoomRepository.findAllByRoomId(roomId);
        userRoomRepository.deleteAllWithFlush(allRoomUsers);
    }

    @Transactional
    public void exitRoomUser(UserRoom userRoom) {
        userRoomRepository.delete(userRoom);
    }

    @Transactional(readOnly = true)
    public boolean isFull(Room room) {
        long roomId = room.getId();
        long firstOrderTeamCount = userRoomRepository.countByRoomIdAndTeam(roomId, Team.RED);
        long secondOrderTeamCount = userRoomRepository.countByRoomIdAndTeam(roomId, Team.BLUE);
        return room.isFull(firstOrderTeamCount, secondOrderTeamCount);
    }
}
