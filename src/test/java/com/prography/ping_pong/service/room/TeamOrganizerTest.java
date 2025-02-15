package com.prography.ping_pong.service.room;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.prography.ping_pong.domain.room.RoomType;
import com.prography.ping_pong.domain.userroom.Team;
import com.prography.ping_pong.exception.custom.PingPongClientErrorException;
import com.prography.ping_pong.view.ResponseMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class TeamOrganizerTest {

    @DisplayName("우선순위 팀의 인원이 모두 차지 않았다면 우선순위 팀을 반환한다")
    @ParameterizedTest
    @EnumSource(RoomType.class)
    void organizeFirstOrderTeam(RoomType roomType) {
        Team firstOrderTeam = Team.RED;
        long notFullCount = roomType.getTeamCapacity() - 1;
        TeamOrganizer organizer = new TeamOrganizer();

        Team organizedTeam = organizer.organize(roomType, notFullCount, 0);

        assertThat(organizedTeam).isEqualTo(firstOrderTeam);
    }

    @DisplayName("한 팀의 용량이 모두 찼다면 차순위 팀을 반환한다")
    @ParameterizedTest
    @EnumSource(RoomType.class)
    void organizeSecondOrderTeam(RoomType roomType) {
        Team secondOrderTeam = Team.BLUE;
        long fullCount = roomType.getTeamCapacity();
        TeamOrganizer organizer = new TeamOrganizer();

        Team organizedTeam = organizer.organize(roomType, fullCount, 0);

        assertThat(organizedTeam).isEqualTo(secondOrderTeam);
    }

    @DisplayName("인원이 모두 찼다면 에러를 반환한다")
    @ParameterizedTest
    @EnumSource(RoomType.class)
    void canNotOrganizeTeamWhenTeamIsFull(RoomType roomType) {
        long fullCount = roomType.getTeamCapacity();
        TeamOrganizer organizer = new TeamOrganizer();

        assertThatThrownBy(() -> organizer.organize(roomType, fullCount, fullCount))
                .isInstanceOf(PingPongClientErrorException.class)
                .hasMessage(ResponseMessage.CLIENT_ERROR.getValue());
    }
}
