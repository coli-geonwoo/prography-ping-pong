package com.prography.pingpong.common;

import com.prography.pingpong.common.fixture.RoomGenerator;
import com.prography.pingpong.common.fixture.UserGenerator;
import com.prography.pingpong.repository.RoomRepository;
import com.prography.pingpong.repository.UserRepository;
import com.prography.pingpong.repository.UserRoomRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(DataBaseCleaner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class BaseServiceTest {

    @Autowired
    protected RoomRepository roomRepository;

    @Autowired
    protected UserRoomRepository userRoomRepository;

    @Autowired
    protected UserGenerator userGenerator;

    @Autowired
    protected RoomGenerator roomGenerator;
}
