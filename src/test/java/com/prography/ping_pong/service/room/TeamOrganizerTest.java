package com.prography.ping_pong.service.room;

import static org.assertj.core.api.Assertions.assertThat;

import com.prography.ping_pong.domain.Team;
import com.prography.ping_pong.domain.room.RoomType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class TeamOrganizerTest {

    @DisplayName("한 팀의 용량이 모두 차지 않았다면 우선순위 팀을 반환한다")
    @ParameterizedTest
    @EnumSource(RoomType.class)
    void organizeFirstOrderTeam(RoomType roomType) {
        Team firstOrderTeam = Team.RED;
        long participantCount = roomType.getTeamCapacity() - 1;
        TeamOrganizer organizer = new TeamOrganizer();

        Team organizedTeam = organizer.organize(roomType, participantCount);

        assertThat(organizedTeam).isEqualTo(firstOrderTeam);
    }

    @DisplayName("한 팀의 용량이 모두 찼다면 차순위 팀을 반환한다")
    @ParameterizedTest
    @EnumSource(RoomType.class)
    void organizeSecondOrderTeam(RoomType roomType) {
        Team secondOrderTeam = Team.BLUE;
        long participantCount = roomType.getTeamCapacity();
        TeamOrganizer organizer = new TeamOrganizer();

        Team organizedTeam = organizer.organize(roomType, participantCount);

        assertThat(organizedTeam).isEqualTo(secondOrderTeam);
    }
}
