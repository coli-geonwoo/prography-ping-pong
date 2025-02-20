package com.prography.pingpong.service.room;

import com.prography.pingpong.domain.room.RoomType;
import com.prography.pingpong.domain.userroom.Team;
import com.prography.pingpong.exception.custom.PingPongClientErrorException;
import com.prography.pingpong.exception.errorcode.ClientErrorCode;
import org.springframework.stereotype.Component;

@Component
public class TeamOrganizer {

    private static final Team FISRT_ORDER_TEAM = Team.RED;
    private static final Team SECOND_ORDER_TEAM = Team.BLUE;

    public Team organize(
            RoomType roomType,
            long firstTeamParticipantCount,
            long secondTeamParticipantCount
    ) {
        long totalParticipantCount = firstTeamParticipantCount + secondTeamParticipantCount;
        if (roomType.isFull(totalParticipantCount)) {
            throw new PingPongClientErrorException(ClientErrorCode.INVALID_REQUEST);
        }

        if (roomType.isLessThanOneTeamCapacity(firstTeamParticipantCount)) {
            return FISRT_ORDER_TEAM;
        }
        return SECOND_ORDER_TEAM;
    }
}
