package com.prography.pingpong.repository.jdbc;

import com.prography.pingpong.domain.user.User;
import java.util.Collection;

public interface UserJdbcRepository {

    void saveAllByBulkQuery(Collection<User> users);
}
