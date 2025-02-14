package com.prography.ping_pong.common;

import com.prography.ping_pong.repository.RoomRepository;
import com.prography.ping_pong.repository.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(DataBaseCleaner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class BaseServiceTest {

    @Autowired
    protected RoomRepository roomRepository;

    @Autowired
    protected UserRepository userRepository;
}
