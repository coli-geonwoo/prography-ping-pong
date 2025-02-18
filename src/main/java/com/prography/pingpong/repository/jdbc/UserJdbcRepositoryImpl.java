package com.prography.pingpong.repository.jdbc;

import com.prography.pingpong.domain.user.User;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

@RequiredArgsConstructor
public class UserJdbcRepositoryImpl implements UserJdbcRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void saveAllByBulkQuery(Collection<User> users) {
        SqlParameterSource[] parameterSources = users.stream()
                .map(this::makeUserParameterSource)
                .toArray(SqlParameterSource[]::new);

        String insertSql = """
                INSERT INTO member (faker_id, name, email, status, created_at, updated_at)
                VALUES (:fakerId, :name, :email, :status, now(), now())
                """;
        namedParameterJdbcTemplate.batchUpdate(insertSql, parameterSources);
    }

    private SqlParameterSource makeUserParameterSource(User user) {
        return new MapSqlParameterSource()
                .addValue("fakerId", user.getFakerId())
                .addValue("name", user.getName())
                .addValue("email", user.getEmail())
                .addValue("status", user.getStatus().name());
    }
}
