package com.prography.pingpong.service.userroom;

import com.prography.pingpong.domain.room.Room;
import com.prography.pingpong.domain.room.RoomType;
import com.prography.pingpong.domain.user.User;
import com.prography.pingpong.domain.userroom.Team;
import com.prography.pingpong.domain.userroom.UserRoom;
import com.prography.pingpong.exception.custom.PingPongClientErrorException;
import com.prography.pingpong.exception.errorcode.ClientErrorCode;
import com.prography.pingpong.repository.UserRoomRepository;
import com.prography.pingpong.service.room.TeamOrganizer;
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
        if (!(canAttend(user) && canEnter(room))) {
            throw new PingPongClientErrorException(ClientErrorCode.INVALID_REQUEST);
        }
        Team team = organizeTeam(room);
        UserRoom userRoom = new UserRoom(user, room, team);
        userRoomRepository.save(userRoom);
    }

    private boolean canAttend(User user) {
        boolean isAlreadyAttended = userRoomRepository.findByUserId(user.getId()).isPresent();
        return user.isActive() && !isAlreadyAttended;
    }

    private boolean canEnter(Room room) {
        List<UserRoom> allRoomUsers = userRoomRepository.findAllByRoomId(room.getId());
        long participantCount = allRoomUsers.size();
        return room.canEnter(participantCount);
    }

    private Team organizeTeam(Room room) {
        long roomId = room.getId();
        long firstOrderTeamCount = userRoomRepository.countByRoomIdAndTeam(roomId, Team.RED);
        long secondOrderTeamCount = userRoomRepository.countByRoomIdAndTeam(roomId, Team.BLUE);
        return teamOrganizer.organize(room.getRoomType(), firstOrderTeamCount, secondOrderTeamCount);
    }

    @Transactional(readOnly = true)
    public UserRoom findByUserIdAndRoomId(long userId, long roomId) {
        return userRoomRepository.findByUserIdAndRoomId(userId, roomId)
                .orElseThrow(() -> new PingPongClientErrorException(ClientErrorCode.INVALID_REQUEST));
    }

    @Transactional
    public void changeTeam(long userId, long roomId) {
        UserRoom userRoom = findByUserIdAndRoomId(userId, roomId);
        if (!canChangeTeam(userRoom)) {
            throw new PingPongClientErrorException(ClientErrorCode.INVALID_REQUEST);
        }
        userRoom.changeTeam();
    }

    private boolean canChangeTeam(UserRoom userRoom) {
        Room room = userRoom.getRoom();
        boolean oppositeTeamFull = isTeamFull(room, userRoom.getOppositeTeam());
        return !oppositeTeamFull && room.isWait();
    }

    @Transactional(readOnly = true)
    public boolean isFull(Room room) {
        boolean firstOrderTeamCount = isTeamFull(room, Team.RED);
        boolean secondOrderTeamCount = isTeamFull(room, Team.BLUE);
        return firstOrderTeamCount && secondOrderTeamCount;
    }

    private boolean isTeamFull(Room room, Team team) {
        RoomType roomType = room.getRoomType();
        long roomId = room.getId();
        long oppositeTeamUserCount = userRoomRepository.countByRoomIdAndTeam(roomId, team);
        return !roomType.isLessThanOneTeamCapacity(oppositeTeamUserCount);
    }

    @Transactional
    public void exitAllRoomUsers(Room room) {
        validateExitCondition(room);
        room.finished();
        List<UserRoom> allRoomUsers = userRoomRepository.findAllByRoomId(room.getId());
        userRoomRepository.deleteAllWithFlush(allRoomUsers);
    }

    @Transactional
    public void exitRoomUser(UserRoom userRoom) {
        validateExitCondition(userRoom.getRoom());
        userRoomRepository.delete(userRoom);
    }

    private void validateExitCondition(Room room) {
        if (!room.isWait()) {
            throw new PingPongClientErrorException(ClientErrorCode.INVALID_REQUEST);
        }
    }

    @Transactional
    public void deleteAllUserRooms() {
        List<UserRoom> allUserRooms = userRoomRepository.findAll();
        userRoomRepository.deleteAllWithFlush(allUserRooms);
    }
}
